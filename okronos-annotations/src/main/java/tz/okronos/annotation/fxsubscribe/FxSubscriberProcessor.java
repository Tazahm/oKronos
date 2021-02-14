package tz.okronos.annotation.fxsubscribe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;


/**
 *  Generates at compile time the proxies used to subscribe to bus event 
 *  ({@link com.google.common.eventbus.EventBus}) and next called a callback
 *  into the FX event main loop.
 *  <p>
 *  All classes C that have methods tagged with an FxSubscribe annotation causes
 *  a proxy C' to be generated. To each method M of C decorated with @{code FxSubscribe}
 *  correspond a method M' into C'. Each such a method M' calls the method M of C with the same argument
 *  into a {@code Platform.runLater()} instance. 
 */
@SupportedAnnotationTypes("tz.okronos.annotation.fxsubscribe.FxSubscribe")
@SupportedSourceVersion(SourceVersion.RELEASE_9)
@AutoService(Processor.class)
public class FxSubscriberProcessor extends AbstractProcessor {

	public static final String GENCLASS_SUFFIX = "$FxSubscriber";
	
	
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
		Map<String, List<Element>> byClass = annotatedElements.stream().collect(Collectors.groupingBy(
				e -> ((TypeElement) e.getEnclosingElement()).getQualifiedName().toString()));
				
		for (Entry<String, List<Element>> entry : byClass.entrySet()) {
			generateClass(entry.getKey(), entry.getValue());
		}		
	}

	private void generateClass(String className, List<? extends Element> annotatedElements) {
		
		Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream().collect(Collectors
				.partitioningBy(element -> ((ExecutableType) element.asType()).getParameterTypes().size() == 1));
		List<Element> otherMethods = annotatedMethods.get(false);		
		
		for (Element element : otherMethods) {
		  processingEnv.getMessager().printMessage(Kind.ERROR,
		    "@FxSubscribe must be applied to a method with a single argument", element);
		}

		List<Element> targetMethods = annotatedMethods.get(true);		
		if (targetMethods.isEmpty()) {
		    return;
		}
					
		Map<String, String> methodMap = targetMethods.stream().collect(Collectors.toMap(
			    setter -> setter.getSimpleName().toString(),
			    setter -> ((ExecutableType) setter.asType())
			      .getParameterTypes().get(0).toString()
			));
		generateClassUnchecked(className, methodMap);
	}

	private void generateClassUnchecked(String className, Map<String, String> methodMap) {
		try {
			generateClassFromMethods(className, methodMap);
		} catch (IOException exception) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "IO error when handling FxSubscribe: " + exception.getMessage());
		}
	
	}
	
	private void generateClassFromMethods(String classFullName, Map<String, String> methodMap) throws IOException {
		String packageName = null;
		int lastDot = classFullName.lastIndexOf('.');
		if (lastDot > 0) {
			packageName = classFullName.substring(0, lastDot);
		}
		String className = classFullName.substring(lastDot + 1);

		String generatedFullClassName = computeGeneratedClassName(classFullName);
		String generatedClassName = generatedFullClassName.substring(lastDot + 1);
		JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(generatedFullClassName);

		try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

			if (packageName != null) {
				out.print("package ");
				out.print(packageName);
				out.println(";");
				out.println();
			}

			out.println();
			out.println("import javafx.application.Platform;");
			out.println("import tz.okronos.annotation.fxsubscribe.FxSubscriber;");
			out.println("import com.google.common.eventbus.Subscribe;");
			out.println("import lombok.Getter;");
			out.println("import lombok.Setter;");

			out.println();
			out.print("public class ");
			out.print(generatedClassName);
			out.print(" implements FxSubscriber<");
			out.print(className);
			out.print("> {");
			out.println();

			out.print("    @Getter @Setter private ");
			out.print(className);
			out.print(" target;");
			out.println();

			List<Entry<String, String>> methods = new ArrayList<>(methodMap.entrySet());
			methods.sort((m1, m2) -> m1.getKey().compareTo(m2.getKey()));
			for (Entry<String, String> method : methods) {
				String methodName = method.getKey();
				String argumentType = method.getValue();

				out.println();
				out.print("    @Subscribe public void ");
				out.print(methodName);
				out.print("(");
				out.print(argumentType);
				out.println(" event) {");
				out.println("        Platform.runLater(() -> target." + methodName + "(event));");
				out.println("    }");
				out.println();
			}

			out.println("}");
		}
	}
}
