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

def draw(enabled):
    gl('glUniform1f', None, I, F)(location(program, b'uEnabled'), enabled)
    gl('glDrawArrays', None, U, I, I)(5, 0, 4)
    out = (C.c_ubyte * len(pixels))()
    gl('glReadPixels', None, I, I, I, I, U, U, P)(0, 0, width, height, 0x1908, 0x1401, out)
    assert gl('glGetError', U)() == 0, 'GL rendering error'
    return bytes(out)

normal, sketch = draw(0), draw(1)
max_error = max(abs(a - b) for a, b in zip(normal, pixels))
assert max_error <= 1, f'Normal video changed: maximum error {max_error}'
changed = sum(normal[i:i+3] != sketch[i:i+3] for i in range(0, len(sketch), 4))
assert changed > width * height * .9, 'Sketch did not transform the whole picture'
assert len(set(sketch[::4])) > 30, 'Sketch lost image detail'
assert sketch == draw(1), 'Sketch texture flickers without a source change'
assert normal == draw(0), 'Turning sketch off did not restore the original'
try:
    from PIL import Image
    output = ROOT / 'app/build/shader-smoke'
    output.mkdir(parents=True, exist_ok=True)
    for name, data in [('normal', normal), ('sketch', sketch)]:
        Image.frombytes('RGBA', (width, height), data).transpose(Image.Transpose.FLIP_TOP_BOTTOM).save(output / f'{name}.png')
except ImportError:
    pass
print(json.dumps({'renderer': renderer, 'normal_max_channel_error': max_error, 'sketch_changed_pixels': changed, 'pixels': width * height, 'deterministic': True}))
egl('eglMakeCurrent', U, P, P, P, P)(display, None, None, None)
egl('eglDestroyContext', U, P, P)(display, context)
egl('eglDestroySurface', U, P, P)(display, surface)
egl('eglTerminate', U, P)(display)
