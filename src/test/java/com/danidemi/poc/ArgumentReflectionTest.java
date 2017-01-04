package com.danidemi.poc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;


public class ArgumentReflectionTest {

	@Test
	public void shouldExtractTheNamesOfTheParameters3() throws NoSuchMethodException, SecurityException, IOException {

		List<String> parameterNames = ArgumentReflection
				.getParameterNames(Clazz.class.getMethod("callMe", String.class, String.class));
		assertEquals("firstName", parameterNames.get(0));
		assertEquals("lastName", parameterNames.get(1));
		assertEquals(2, parameterNames.size());

	}

	public static final class Clazz {

		public void callMe(String firstName, String lastName) {
		}

	}



}
