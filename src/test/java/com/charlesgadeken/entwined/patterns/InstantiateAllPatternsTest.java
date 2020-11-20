package com.charlesgadeken.entwined.patterns;

import com.charlesgadeken.entwined.model.Entwined;
import heronarts.lx.LX;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.Reflections;

@TestInstance(Lifecycle.PER_CLASS)
public class InstantiateAllPatternsTest {

    Entwined model;

    @BeforeAll
    void init() {
        this.model = Entwined.fromConfigs(new LX());
    }

    Stream<Class<?>> findPatterns() {
        Reflections reflection = new Reflections("com.charlesgadeken");
        Set<Class<? extends EntwinedBasePattern>> patterns =
                reflection.getSubTypesOf(EntwinedBasePattern.class);
        List list = Arrays.asList(patterns.toArray());

        return list.stream();
    }

    @ParameterizedTest
    @MethodSource("findPatterns")
    void validatePatternsFromBaseInstantiate(Class<?> cls)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException,
                    InvocationTargetException {
        cls.getConstructor(LX.class).newInstance(new LX(model));
    }
}