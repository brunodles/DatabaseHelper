package com.github.brunodles.databasehelper.aspect;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class InjectCreateAspect {
    private static final String TAG = "InjectCreateAspect";

    @Pointcut("execution(@com.github.brunodles.databasehelper.annotation.InjectCreate * *(..))")
    public void methodAnnotatedWithInjectCreate() {
        Log.d(TAG, "methodAnnotatedWithInjectCreate: methodAnnotatedWithInjectCreate");
    }

    @After("methodAnnotatedWithInjectCreate()")
    public void weaveJoinPoint(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class declaringType = methodSignature.getDeclaringType();
        String methodName = methodSignature.getName();

        SQLiteDatabase db = (SQLiteDatabase) joinPoint.getArgs()[0];
        Log.d(TAG, String.format("weaveJoinPoint: %s.%s : dbPath: %s",
                declaringType.getSimpleName(), methodName, db.getPath()));

        try {
            Class<?> aClass = Class.forName(declaringType.getCanonicalName() + "_Helper");
            Method onCreate = aClass.getDeclaredMethod("onCreate", SQLiteDatabase.class);
            onCreate.invoke(null, db);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Failed to find the helper class", e);
        } catch (Exception e) {
            Log.e(TAG, "weaveJoinPoint: ", e);
        }
    }
}
