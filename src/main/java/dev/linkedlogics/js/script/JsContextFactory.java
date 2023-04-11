package dev.linkedlogics.js.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class JsContextFactory extends ContextFactory {
    @Override
    protected boolean hasFeature(Context cx, int featureIndex) {
        switch (featureIndex) {
            case Context.FEATURE_ENABLE_JAVA_MAP_ACCESS:
                return true;
            case Context.FEATURE_ENHANCED_JAVA_ACCESS:
            	return true;
        }
        return super.hasFeature(cx, featureIndex);
    }
    
//    protected Context makeContext() {
//        Context cx = super.makeContext();
//        // use the JavaAdapter to create native Java objects
//        cx.setWrapFactory(new JavaObjectWrapFactory());
//        return cx;
//    }
//    
//    private static class JavaObjectWrapFactory extends WrapFactory {
//        public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
//            if (obj instanceof NativeJavaObject) {
//                return obj;
//            } else if (obj instanceof Scriptable) {
////            	cx.jsToJava(obj, staticType)
//             return null;
////            	return JavaAdapter..construct(cx, staticType, (Scriptable) obj);
//            } else {
//                return super.wrap(cx, scope, obj, staticType);
//            }
//        }
//    }
}
