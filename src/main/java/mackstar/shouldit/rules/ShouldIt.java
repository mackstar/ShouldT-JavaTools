package mackstar.shouldit.rules;

import java.lang.annotation.Annotation;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import mackstar.shouldit.annotations.ShouldItContext;
import mackstar.shouldit.annotations.It;
import mackstar.shouldit.services.*;

public class ShouldIt extends TestWatcher {
	
	Description description = null;
	
    public void setOutputFile(String fileName) {
    	JsonWrite.setOutputFile(fileName);
    }
	
	@Override
    protected void succeeded(Description description) {
		this.description = description;
		processResult("PASSED");
    }
    
	@Override
    protected void failed(Throwable e, Description description) {
		this.description = description;
		processResult("FAILED");
		
    }
    
    private void processResult(String outcome) {
		String context = getContext();
		String it = getIt();
		if ((it != null && context != null)) {
			JsonWrite.add(context, it, outcome);
			JsonWrite.write();
		}
    }
    
    private String getContext() {
    	Class<?> className = null;
		try {
			className = Class.forName(description.getClassName());
		} catch (ClassNotFoundException e) {
			return null;
		}
		Annotation annotation = className.getAnnotation(ShouldItContext.class);
		ShouldItContext context = (ShouldItContext) annotation;
		if (context == null) {
			return null;
		}
    	return context.value();
    }
    
    private String getIt() {
    	Annotation annotation = description.getAnnotation(It.class);
		It it = (It) annotation;
		if (it == null) {
			return null;
		}
		return it.value();
    }
	
}