#!/usr/bin/env python3
"""Compile and draw the APK's exact GLES shaders using a local offscreen EGL context.
This checks shader behavior, not Android hardware decoding or phone performance.
"""
import ctypes as C
import ctypes.util
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
EGL = C.CDLL(ctypes.util.find_library('EGL'))
I, U, F, P = C.c_int, C.c_uint, C.c_float, C.c_void_p

def egl(name, result, *args):
    fn = getattr(EGL, name)
    fn.restype, fn.argtypes = result, list(args)
    return fn

proc = egl('eglGetProcAddress', P, C.c_char_p)
platform = C.CFUNCTYPE(P, U, P, C.POINTER(I))(proc(b'eglGetPlatformDisplayEXT'))
display = platform(0x31DD, None, None)  # EGL_PLATFORM_SURFACELESS_MESA
major, minor = I(), I()
assert egl('eglInitialize', U, P, C.POINTER(I), C.POINTER(I))(display, C.byref(major), C.byref(minor)), 'EGL initialization failed'
assert egl('eglBindAPI', U, U)(0x30A0), 'OpenGL ES API unavailable'
attrs = (I * 13)(0x3033, 1, 0x3040, 4, 0x3024, 8, 0x3023, 8, 0x3022, 8, 0x3021, 8, 0x3038)
config, count = P(), I()
assert egl('eglChooseConfig', U, P, C.POINTER(I), C.POINTER(P), I, C.POINTER(I))(display, attrs, C.byref(config), 1, C.byref(count)) and count.value
width, height = 256, 128
surface_attrs = (I * 5)(0x3057, width, 0x3056, height, 0x3038)
surface = egl('eglCreatePbufferSurface', P, P, P, C.POINTER(I))(display, config, surface_attrs)
context_attrs = (I * 3)(0x3098, 2, 0x3038)
context = egl('eglCreateContext', P, P, P, P, C.POINTER(I))(display, config, None, context_attrs)
assert surface and context
assert egl('eglMakeCurrent', U, P, P, P, P)(display, surface, surface, context)

def gl(name, result, *args):
    address = proc(name.encode())
    assert address, name
    return C.CFUNCTYPE(result, *args)(address)

get_string = gl('glGetString', C.c_char_p, U)
renderer = get_string(0x1F01).decode()
create_shader = gl('glCreateShader', U, U)
shader_source = gl('glShaderSource', None, U, I, C.POINTER(C.c_char_p), C.POINTER(I))
compile_shader = gl('glCompileShader', None, U)
get_shader = gl('glGetShaderiv', None, U, U, C.POINTER(I))
shader_log = gl('glGetShaderInfoLog', None, U, I, P, C.c_char_p)

def compile_one(kind, path):
    shader = create_shader(kind)
    text = C.c_char_p(path.read_bytes())
    shader_source(shader, 1, C.byref(text), None)
    compile_shader(shader)
    ok = I()
    get_shader(shader, 0x8B81, C.byref(ok))
    log = C.create_string_buffer(8192)
    shader_log(shader, len(log), None, log)
    assert ok.value, log.value.decode()
    return shader

vertex = compile_one(0x8B31, ROOT / 'app/src/main/assets/shaders/pulse_sketch.vert')
fragment = compile_one(0x8B30, ROOT / 'app/src/main/assets/shaders/pulse_sketch.frag')
program = gl('glCreateProgram', U)()
attach = gl('glAttachShader', None, U, U)
attach(program, vertex)
attach(program, fragment)
gl('glLinkProgram', None, U)(program)
ok = I()
gl('glGetProgramiv', None, U, U, C.POINTER(I))(program, 0x8B82, C.byref(ok))
assert ok.value, 'Shader linking failed'
gl('glUseProgram', None, U)(program)
attribute = gl('glGetAttribLocation', I, U, C.c_char_p)(program, b'aPosition')
vertices = (F * 16)(-1, -1, 0, 1, -1, 1, 0, 1, 1, -1, 0, 1, 1, 1, 0, 1)
gl('glEnableVertexAttribArray', None, U)(attribute)
gl('glVertexAttribPointer', None, U, I, U, U, I, P)(attribute, 4, 0x1406, 0, 0, C.cast(vertices, P))
texture = U()
gl('glGenTextures', None, I, C.POINTER(U))(1, C.byref(texture))
gl('glBindTexture', None, U, U)(0x0DE1, texture.value)
parameter = gl('glTexParameteri', None, U, U, I)
for key, value in [(0x2801, 0x2601), (0x2800, 0x2601), (0x2802, 0x812F), (0x2803, 0x812F)]:
    parameter(0x0DE1, key, value)
pixels = bytearray()
for y in range(height):
    for x in range(width):
        rgb = (x, int(y * 255 / (height - 1)), 255 - x)
        if 70 <= x < 180 and 30 <= y < 100:
            rgb = (35, 35, 35)
        pixels.extend((*rgb, 255))
source = (C.c_ubyte * len(pixels)).from_buffer_copy(pixels)
gl('glTexImage2D', None, U, I, I, I, I, I, U, U, P)(0x0DE1, 0, 0x1908, width, height, 0, 0x1908, 0x1401, source)
location = gl('glGetUniformLocation', I, U, C.c_char_p)
gl('glUniform1i', None, I, I)(location(program, b'uTexSampler'), 0)
gl('glUniform2f', None, I, F, F)(location(program, b'uPixel'), 1 / width, 1 / height)
gl('glUniform3f', None, I, F, F, F)(location(program, b'uLuma'), .2126, .7152, .0722)
gl('glViewport', None, I, I, I, I)(0, 0, width, height)

def draw(enabled, style=1, preview=0):
    gl('glUniform1f', None, I, F)(location(program, b'uEnabled'), enabled)
    gl('glUniform1f', None, I, F)(location(program, b'uStyle'), style)
    gl('glUniform1f', None, I, F)(location(program, b'uPreview'), preview)
    gl('glDrawArrays', None, U, I, I)(5, 0, 4)
    out = (C.c_ubyte * len(pixels))()
    gl('glReadPixels', None, I, I, I, I, U, U, P)(0, 0, width, height, 0x1908, 0x1401, out)
    assert gl('glGetError', U)() == 0, 'GL rendering error'
    return bytes(out)

normal = draw(0)
max_error = max(abs(a - b) for a, b in zip(normal, pixels))
assert max_error <= 1, f'Normal video changed: maximum error {max_error}'
styles = ['fine-pencil', 'charcoal', 'ink-hatch', 'color-pencil', 'white-outline', 'comic']
results = {}
frames = {}
import hashlib
for style, name in enumerate(styles, 1):
    sketch = draw(1, style)
    changed = sum(normal[i:i+3] != sketch[i:i+3] for i in range(0, len(sketch), 4))
    assert changed > width * height * .9, f'{name} did not transform the picture'
    assert sketch == draw(1, style), f'{name} flickers without a source change'
    assert normal == draw(0, style), f'{name} changes normal video when off'
    results[name] = {'changed_pixels': changed, 'sha256': hashlib.sha256(sketch).hexdigest()}
    frames[name] = sketch
assert len({r['sha256'] for r in results.values()}) == 6, 'Styles render identically'
assert len(set(frames['fine-pencil'][::4])) > 30, 'Fine pencil lost image detail'
outline = frames['white-outline']
assert sum(outline[i] < 10 for i in range(0, len(outline), 4)) > width * height * .6, 'Outline background is not black'
for name in ['charcoal', 'ink-hatch', 'white-outline']:
    assert all(max(frames[name][i:i+3]) - min(frames[name][i:i+3]) <= 1 for i in range(0, len(normal), 4)), f'{name} should be monochrome'
for name in ['color-pencil', 'comic']:
    assert sum(max(frames[name][i:i+3]) - min(frames[name][i:i+3]) > 20 for i in range(0, len(normal), 4)) > width * height * .2, f'{name} lost source color'
# Compare the thumbnail transfer with the same style's linear video result.
previews = {'normal': draw(0, 1, 1)}
for style, name in enumerate(styles, 1):
    previews[name] = draw(1, style, 1)
for name, linear in {'normal': normal, **frames}.items():
    image = previews[name]
    for i in range(0, len(image), 4):
        for channel in range(3):
            # Linear output was quantized to 8-bit for this test, so allow the
            # one-code error to grow under the display transfer near black.
            lo = round((max(0, linear[i+channel] - 1) / 255) ** (1 / 2.2) * 255)
            hi = round((min(255, linear[i+channel] + 1) / 255) ** (1 / 2.2) * 255)
            assert lo - 1 <= image[i+channel] <= hi + 1, f'{name} preview differs from the video shader'
        assert image[i+3] == linear[i+3]
    assert draw(0) == normal, 'Preview rendering changed subsequent normal video'
# Exercise the preview readback target and restore the original video target.
fbo, preview_texture = U(), U()
gl('glGenTextures', None, I, C.POINTER(U))(1, C.byref(preview_texture))
gl('glBindTexture', None, U, U)(0x0DE1, preview_texture.value)
for key, value in [(0x2801, 0x2601), (0x2800, 0x2601), (0x2802, 0x812F), (0x2803, 0x812F)]:
    parameter(0x0DE1, key, value)
gl('glTexImage2D', None, U, I, I, I, I, I, U, U, P)(0x0DE1, 0, 0x1908, width, height, 0, 0x1908, 0x1401, None)
gl('glGenFramebuffers', None, I, C.POINTER(U))(1, C.byref(fbo))
gl('glBindFramebuffer', None, U, U)(0x8D40, fbo.value)
gl('glFramebufferTexture2D', None, U, U, U, U, I)(0x8D40, 0x8CE0, 0x0DE1, preview_texture.value, 0)
assert gl('glCheckFramebufferStatus', U, U)(0x8D40) == 0x8CD5, 'Preview framebuffer incomplete'
gl('glBindTexture', None, U, U)(0x0DE1, texture.value)
for style, name in enumerate(['normal', *styles]):
    assert draw(style != 0, max(1, style), 1) == previews[name], f'{name} offscreen readback differs'
gl('glBindFramebuffer', None, U, U)(0x8D40, 0)
gl('glDeleteFramebuffers', None, I, C.POINTER(U))(1, C.byref(fbo))
gl('glDeleteTextures', None, I, C.POINTER(U))(1, C.byref(preview_texture))
assert draw(0) == normal, 'Preview target restore changed video output'
try:
    from PIL import Image
    output = ROOT / 'app/build/shader-smoke'
    output.mkdir(parents=True, exist_ok=True)
    for name, data in {'normal': normal, **frames, **{'preview-' + name: frame for name, frame in previews.items()}}.items():
        Image.frombytes('RGBA', (width, height), data).transpose(Image.Transpose.FLIP_TOP_BOTTOM).save(output / f'{name}.png')
except ImportError:
    pass
print(json.dumps({'renderer': renderer, 'normal_max_channel_error': max_error, 'styles': results, 'pixels': width * height, 'deterministic': True, 'preview_transfer_checked': True, 'preview_framebuffer_checked': True}))
egl('eglMakeCurrent', U, P, P, P, P)(display, None, None, None)
egl('eglDestroyContext', U, P, P)(display, context)
egl('eglDestroySurface', U, P, P)(display, surface)
egl('eglTerminate', U, P)(display)
