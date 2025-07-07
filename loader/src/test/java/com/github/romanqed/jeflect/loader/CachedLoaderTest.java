package com.github.romanqed.jeflect.loader;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public final class CachedLoaderTest {

    @Test
    public void testCachedClass() throws Exception {
        var loader = new DefineClassLoader();
        assertNull(loader.load("TestClass"));
        var cached = new CachedClassLoader(loader, new ResourcesCache());
        var loaded = cached.load("TestClass");
        assertNotNull(loaded);
        var object = loaded.getDeclaredConstructor(String.class).newInstance("value");
        var method = loaded.getDeclaredMethod("getValue");
        method.setAccessible(true);
        assertEquals("value", object.toString());
        assertEquals("value", method.invoke(object));
    }

    @Test
    public void testNoClass() {
        var cached = new CachedClassLoader(new DefineClassLoader(), new ResourcesCache());
        assertNull(cached.load("TestClass1"));
    }

    static final class ResourcesCache implements ClassCache {

        @Override
        public byte[] get(String name) {
            // .buffer postfix to avoid normal class loading from jar
            try (var buffer = getClass().getResourceAsStream(name + ".buffer")) {
                if (buffer == null) {
                    return null;
                }
                return buffer.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void set(String name, byte[] buffer) {
            throw new UnsupportedOperationException("Cannot update resources");
        }
    }
}
