package tz.okronos.annotation.lateralizedbean;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;

import tz.okronos.core.Lateralized;
import tz.okronos.core.LateralizedPairController;
import tz.okronos.core.SimpleLateralizedPair;


/**
 *  At compile time, for each class annotated with the LateralizedConfiguration element, generates 
 *  two spring instances of the annotated class, one for the left side and another for the right side.
 *  <p>
 *  See {@link LateralizedConfiguration}.
 */
@SupportedAnnotationTypes("tz.okronos.annotation.lateralizedbean.LateralizedConfiguration")
@SupportedSourceVersion(SourceVersion.RELEASE_9)
@AutoService(Processor.class)
public class LateralizedConfigurationProcessor extends AbstractProcessor {

	public static final String LateralizedPairFullName = SimpleLateralizedPair.class.getName();
	public static final String LateralizedPairControllerFullName = LateralizedPairController.class.getName();
	public static final String LateralizedSuffix = Lateralized.class.getSimpleName();
	public static final String GENCLASS_SUFFIX = "$" + LateralizedSuffix;
	
	public static String computeGeneratedClassName(String reference) {
		return reference + GENCLASS_SUFFIX;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement annotation : annotations) {
			processingEnv.getMessager().printMessage(Kind.NOTE, "Handling annotation " + annotation.getQualifiedName());
			generateAllClasses(roundEnv.getElementsAnnotatedWith(annotation));
		}
		return true;
	}

	private void generateAllClasses(Set<? extends Element> annotatedElements) {
		for (Element element : annotatedElements) {
			generateClassUnchecked(element);
		}
	}


	
	private void generateClassUnchecked(Element element) {
		try {
			if (element.getKind() == ElementKind.CLASS) {
				generateClass((TypeElement) element);
			} else {
				processingEnv.getMessager().printMessage(Kind.ERROR, "Not a class : " + element.getSimpleName().toString());
			}
		} catch (IOException | ClassNotFoundException exception) {
			processingEnv.getMessager().printMessage(Kind.ERROR,
					"IO error when handling LateralizedConfiguration : " + exception.getMessage());
		}

	}

	private void generateClass(TypeElement element) throws IOException, ClassNotFoundException {
		GenerationData gendata = new GenerationData();
		gendata.element = element;
		gendata.className = element.getSimpleName().toString();		
		gendata.classFullName = element.getQualifiedName().toString();		
		gendata.packageName = gendata.classFullName.substring(0, gendata.classFullName.lastIndexOf('.'));		
		gendata.generatedClassName = computeGeneratedClassName(gendata.className);
		gendata.generatedFullClassName = gendata.packageName + "." + gendata.generatedClassName;				
		gendata.generatedBeanName = gendata.className.substring(0, 1).toLowerCase() + gendata.className.substring(1);
		gendata.generatedLateralizedBeanName = gendata.className.substring(0, 1).toLowerCase()
				+ gendata.className.substring(1) + LateralizedSuffix;			
			
		gendata.postConstructMethods = gendata.element.getEnclosedElements().stream()
				.filter(e -> e.getKind() == ElementKind.METHOD)
				.filter(e -> e.getAnnotation(LateralizedPostConstruct.class) != null)
				.map(e -> (ExecutableElement) e)
				.collect(Collectors.toList());

		gendata.beanMethods = gendata.element.getEnclosedElements().stream()
				.filter(e -> e.getKind() == ElementKind.METHOD)
				.filter(e -> e.getAnnotation(LateralizedBean.class) != null)
				.map(e -> (ExecutableElement) e)
				.collect(Collectors.toList());

		JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(gendata.generatedFullClassName);
		try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
			gendata.out = out;

			out.println("package " + gendata.packageName + ";");	

			out.println();
			if (! gendata.postConstructMethods.isEmpty()) {
				out.println("import javax.annotation.PostConstruct;");
			}
			out.println("import org.springframework.context.annotation.Bean;");
			out.println("import org.springframework.context.annotation.Configuration;");
			out.println("import org.springframework.context.annotation.Scope;");
			out.println("import " + LateralizedPairFullName + ";");
			out.println("import " + LateralizedPairControllerFullName + ";");
			
			generateClassAccessor(gendata);
			generatePostConstructMethods(gendata);
			generateAccessors(gendata);

			out.println("}");
		}
	}

	private void generateClassAccessor(GenerationData gendata) {
		gendata.out.println();
		gendata.out.println("@Configuration");
		gendata.out.println("public class " + gendata.generatedClassName + " {");

		gendata.out.println("");
		gendata.out.println("    @Bean");
		gendata.out.println("    @Scope(\"prototype\")");
		gendata.out.println("    public " + gendata.className + " " + gendata.generatedBeanName + "() {");
		gendata.out.println("        return new " + gendata.className + "();");
		gendata.out.println("    }");

		gendata.out.println("");
		gendata.out.println("    @Bean");
		gendata.out.println("    public " + LateralizedPairControllerFullName + "<" + gendata.className + "> " + gendata.generatedLateralizedBeanName + "() {");
		gendata.out.println("	     return new " + LateralizedPairControllerFullName + "<>(" + gendata.generatedBeanName + "(), " + gendata.generatedBeanName + "());");
		gendata.out.println("    }");
		
		gendata.out.println("");	   
	}
	
	private void generatePostConstructMethods(GenerationData gendata) {
			for (ExecutableElement method : gendata.postConstructMethods) {
				generatePostConstructMethod(gendata, method);
			}	    	
	    }
	
	private void generatePostConstructMethod(GenerationData gendata, ExecutableElement method) {		
		String methodName = method.getSimpleName().toString();		
		
		gendata.out.println("");
		gendata.out.println("    @PostConstruct");
		gendata.out.println("    public void " + methodName + "() {");
		gendata.out.println("        " +  gendata.generatedLateralizedBeanName + "().getLeft()." + methodName + "();");
		gendata.out.println("        " +  gendata.generatedLateralizedBeanName + "().getRight()." + methodName + "();");
		gendata.out.println("    }");
	}
	
	private void generateAccessors(GenerationData gendata) throws ClassNotFoundException {
		for (ExecutableElement method : gendata.beanMethods) {
			generateAccessor(gendata, method);
		}
	}

	private void generateAccessor(GenerationData gendata, ExecutableElement method) {
		String returnName = method.getReturnType().toString();
		String methodName = method.getSimpleName().toString();
		String generatedMethodName = methodName + LateralizedSuffix;
		
		gendata.out.println("");
		gendata.out.println("    @Bean");
		gendata.out.println("    public " + LateralizedPairFullName + "<" + returnName + "> " + generatedMethodName + "() {");
		gendata.out.println("	     return new " + LateralizedPairFullName + "<>(");
		gendata.out.println("		     " + gendata.generatedLateralizedBeanName + "().getLeft()." + methodName + "(),");
		gendata.out.println("		     " + gendata.generatedLateralizedBeanName + "().getRight()." + methodName + "());");
		gendata.out.println("    }");
	}

	private static class GenerationData {
		Element element; 
		List<ExecutableElement> postConstructMethods;
		List<ExecutableElement> beanMethods;
		PrintWriter out;
		String packageName;
		String className;
		String classFullName;
		String generatedClassName;
		String generatedFullClassName;
		String generatedBeanName;
		String generatedLateralizedBeanName; 
	}
}
