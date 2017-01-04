package com.danidemi.poc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

public class Utils {
	/**
	 * Returns a list containing one parameter name for each argument accepted
	 * by the given constructor. If the class was compiled with debugging
	 * symbols, the parameter names will match those provided in the Java source
	 * code. Otherwise, a generic "arg" parameter name is generated ("arg0" for
	 * the first argument, "arg1" for the second...).
	 * 
	 * This method relies on the constructor's class loader to locate the
	 * bytecode resource that defined its class.
	 * 
	 * @param theMethod
	 * @return
	 * @throws IOException
	 */
	public static List<String> getParameterNames(Method theMethod) throws IOException {
		Class<?> declaringClass = theMethod.getDeclaringClass();
		ClassLoader declaringClassLoader = declaringClass.getClassLoader();

		Type declaringType = Type.getType(declaringClass);
		String constructorDescriptor = Type.getMethodDescriptor(theMethod);
		String url = declaringType.getInternalName() + ".class";

		InputStream classFileInputStream = declaringClassLoader.getResourceAsStream(url);
		if (classFileInputStream == null) {
			throw new IllegalArgumentException(
					"The constructor's class loader cannot find the bytecode that defined the constructor's class (URL: "
							+ url + ")");
		}

		ClassNode classNode;
		try {
			classNode = new ClassNode();
			ClassReader classReader = new ClassReader(classFileInputStream);
			classReader.accept(classNode, 0);
		} finally {
			classFileInputStream.close();
		}

		@SuppressWarnings("unchecked")
		List<MethodNode> methods = classNode.methods;
		for (MethodNode method : methods) {
			if (method.name.equals(theMethod.getName()) && method.desc.equals(constructorDescriptor)) {
				Type[] argumentTypes = Type.getArgumentTypes(method.desc);
				List<String> parameterNames = new ArrayList<String>(argumentTypes.length);

				@SuppressWarnings("unchecked")
				List<LocalVariableNode> localVariables = method.localVariables;
				for (int i = 1; i <= argumentTypes.length; i++) {
					// The first local variable actually represents the "this"
					// object if the method is not static!
					parameterNames.add(localVariables.get(i).name);
				}

				return parameterNames;
			}
		}

		return null;
	}
}
