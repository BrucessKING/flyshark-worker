package top.bigking.flyshark.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.*;
import top.bigking.flyshark.entity.MyURL;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomMethodResolver implements MethodResolver {
    @Override
    public MethodExecutor resolve(EvaluationContext evaluationContext, Object targetObject, String name, List<TypeDescriptor> argumentTypes) throws AccessException {
        if (targetObject instanceof Map) {
            if (name.length() > 0)
                return new PocFunctionExecutor(name, argumentTypes);
        } else if (targetObject instanceof byte[]) {
            return new ByteFunctionExecutor(name, targetObject, argumentTypes);
        } else if (targetObject instanceof String) {
            return new RawFunctionExecutor(name, targetObject, argumentTypes);
        } else if (targetObject instanceof List) {
            return new ListFunctionExecutor(name, targetObject, argumentTypes);
        } else if (targetObject instanceof MyURL myURL) {
            return new MyURLFunctionExecutor(name, targetObject, argumentTypes);
        }
        return null;
    }
    static class PocFunctionExecutor implements MethodExecutor {
        private String functionName;
        private List<TypeDescriptor> argumentTypes;
        public PocFunctionExecutor() {}
        public PocFunctionExecutor(String functionName, List<TypeDescriptor> argumentTypes) {
            this.functionName = functionName;
            this.argumentTypes = argumentTypes;
        }
        @Override
        public TypedValue execute(EvaluationContext evaluationContext, Object o, Object... objects) throws AccessException {
            if (this.functionName.equals("wait")) {
                Class<?> [] clazz = new Class[this.argumentTypes.size() + 1];
                for (int i = 0; i < this.argumentTypes.size(); i++) {
                    clazz[i] = this.argumentTypes.get(i).getType();
                }
                clazz[this.argumentTypes.size()] = o.getClass();
                Object[] objects1 = new Object[objects.length + 1];
                for (int i = 0; i < objects.length; i++) {
                    objects1[i] = objects[i];
                }
                objects1[objects.length] = o;
                try {
                    Object invoke = PocFunction.class.getDeclaredMethod(this.functionName, clazz).invoke(null, objects1);
                    log.info(invoke.toString());
                    return new TypedValue(invoke);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (this.functionName.equals("int")) {
                if (objects.length == 1 && objects[0] instanceof ArrayList arrayList) {
                    if (arrayList.get(0) instanceof String s) {
                        return new TypedValue(Integer.parseInt(s));
                    }
                }
            }
            Class<?> [] clazz = new Class[this.argumentTypes.size()];
            for (int i = 0; i < this.argumentTypes.size(); i++) {
                clazz[i] = this.argumentTypes.get(i).getType();
            }
            try {
                Object invoke = PocFunction.class.getDeclaredMethod(this.functionName, clazz).invoke(null, objects);
                log.info(invoke.toString());
                return new TypedValue(invoke);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    static class ByteFunctionExecutor implements MethodExecutor {
        private String functionName;
        private List<TypeDescriptor> argumentTypes;
        private Object targetObject;
        public ByteFunctionExecutor() {}
        public ByteFunctionExecutor(String functionName, Object targetObject, List<TypeDescriptor> argumentTypes) {
            this.functionName = functionName;
            this.targetObject = targetObject;
            this.argumentTypes = argumentTypes;
        }
        @Override
        public TypedValue execute(EvaluationContext evaluationContext, Object o, Object... objects) throws AccessException {
            Class<?> [] clazz = new Class[this.argumentTypes.size() + 1];
            for (int i = 0; i < this.argumentTypes.size(); i++) {
                clazz[i] = this.argumentTypes.get(i).getType();
            }
            clazz[this.argumentTypes.size()] = o.getClass();
            Object[] objects1 = new Object[objects.length + 1];
            for (int i = 0; i < objects.length; i++) {
                objects1[i] = objects[i];
            }
            objects1[objects.length] = o;
            try {
                Object invoke = ByteFunction.class.getDeclaredMethod(this.functionName, clazz).invoke(null, objects1);
                log.info(invoke.toString());
                return new TypedValue(invoke);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    static class RawFunctionExecutor implements MethodExecutor {
        private String functionName;
        private List<TypeDescriptor> argumentTypes;
        private Object targetObject;
        public RawFunctionExecutor(){}
        public RawFunctionExecutor(String functionName, Object targetObject, List<TypeDescriptor> argumentTypes) {
            this.functionName = functionName;
            this.targetObject = targetObject;
            this.argumentTypes = argumentTypes;
        }
        @Override
        public TypedValue execute(EvaluationContext context, Object o, Object... objects) throws AccessException {
            Class<?> [] clazz = new Class[this.argumentTypes.size() + 1];
            for (int i = 0; i < this.argumentTypes.size(); i++) {
                clazz[i] = this.argumentTypes.get(i).getType();
            }
            clazz[this.argumentTypes.size()] = o.getClass();
            Object[] objects1 = new Object[objects.length + 1];
            for (int i = 0; i < objects.length; i++) {
                objects1[i] = objects[i];
            }
            objects1[objects.length] = o;
            try {
                Object invoke = RawFunction.class.getDeclaredMethod(this.functionName, clazz).invoke(null, objects1);
                log.info(invoke.toString());
                return new TypedValue(invoke);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    static class ListFunctionExecutor implements MethodExecutor {
        private String functionName;
        private List<TypeDescriptor> argumentTypes;
        private Object targetObject;
        public ListFunctionExecutor() {
        }
        public ListFunctionExecutor(String functionName, Object targetObject, List<TypeDescriptor> argumentTypes) {
            this.functionName = functionName;
            this.targetObject = targetObject;
            this.argumentTypes = argumentTypes;
        }
        @Override
        public TypedValue execute(EvaluationContext context, Object o, Object... objects) throws AccessException {
            Class<?> [] clazz = new Class[this.argumentTypes.size() + 1];
            for (int i = 0; i < this.argumentTypes.size(); i++) {
                clazz[i] = this.argumentTypes.get(i).getType();
            }
            clazz[this.argumentTypes.size()] = o.getClass();
            Object[] objects1 = new Object[objects.length + 1];
            for (int i = 0; i < objects.length; i++) {
                objects1[i] = objects[i];
            }
            objects1[objects.length] = o;
            try {
                Object invoke = ListFunction.class.getDeclaredMethod(this.functionName, clazz).invoke(null, objects1);
//                log.info(invoke.toString());
                return new TypedValue(invoke);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    static class MyURLFunctionExecutor implements MethodExecutor {
        private String functionName;
        private List<TypeDescriptor> argumentTypes;
        private Object targetObject;
        public MyURLFunctionExecutor() {
        }
        public MyURLFunctionExecutor(String functionName, Object targetObject, List<TypeDescriptor> argumentTypes) {
            this.functionName = functionName;
            this.targetObject = targetObject;
            this.argumentTypes = argumentTypes;
        }
        @Override
        public TypedValue execute(EvaluationContext context, Object o, Object... objects) throws AccessException {
            Class<?> [] clazz = new Class[this.argumentTypes.size() + 1];
            for (int i = 0; i < this.argumentTypes.size(); i++) {
                clazz[i] = this.argumentTypes.get(i).getType();
            }
            clazz[this.argumentTypes.size()] = o.getClass();
            Object[] objects1 = new Object[objects.length + 1];
            for (int i = 0; i < objects.length; i++) {
                objects1[i] = objects[i];
            }
            objects1[objects.length] = o;
            try {
                Object invoke = MyURLFunction.class.getDeclaredMethod(this.functionName, clazz).invoke(null, objects1);
//                log.info(invoke.toString());
                return new TypedValue(invoke);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

