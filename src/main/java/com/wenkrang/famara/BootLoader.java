package com.wenkrang.famara;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 插件引导加载器，负责使用自定义ClassLoader动态加载并运行主类。
 * 该类不依赖任何外部库，以确保兼容性和稳定性。
 */
//TODO:完善卸载机制，添加远程更新
public final class BootLoader extends JavaPlugin {

    /**
     * 存储已加载的ClassLoader实例，用于避免内存泄漏。
     */
    private static final Map<String, HotClassLoader> CLASS_LOADERS = new ConcurrentHashMap<>();

    /**
     * 当前正在使用的插件主类实例。
     */
    private static volatile Object currentInstance;

    /**
     * 自定义类加载器，用于从指定目录加载类文件，并破坏双亲委派机制。
     */
    public static class HotClassLoader extends ClassLoader {

        /**
         * 已加载类的缓存，防止重复加载。
         */
        private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

        /**
         * 类文件所在的目录路径。
         */
        private final String classDir;

        /**
         * 构造方法，初始化类加载器。
         *
         * @param classDir 类文件所在的目录路径
         */
        public HotClassLoader(String classDir) {
            super(BootLoader.class.getClassLoader()); // 设置父类加载器
            this.classDir = classDir;
        }

        /**
         * 重写loadClass方法，实现自定义类加载逻辑。
         *
         * @param name    类名
         * @param resolve 是否需要解析类
         * @return 加载的类
         * @throws ClassNotFoundException 类未找到异常
         */
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class<?> clazz;
            // 1. 优先尝试当前ClassLoader加载（关键：确保依赖类也通过当前ClassLoader加载）
            try {
                clazz = findClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } catch (ClassNotFoundException e) {
                // 2. 如果当前ClassLoader找不到，再尝试父类加载器（仅用于系统类）
                if (getParent() != null) {
                    return getParent().loadClass(name);
                }
                throw e;
            }
        }

        /**
         * 查找并加载指定名称的类。
         *
         * @param name 类名
         * @return 加载的类对象
         * @throws ClassNotFoundException 类未找到异常
         */
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            // 1. 检查是否已缓存
            Class<?> cached = loadedClasses.get(name);
            if (cached != null) {
                return cached;
            }

            // 2. 从指定目录加载class文件
            String classPath = name.replace('.', '/') + ".class";
            File classFile = new File(classDir, classPath);

            // 检查文件是否存在
            if (!classFile.exists() || !classFile.isFile()) {
                throw new ClassNotFoundException("Class not found: " + name + " at " + classFile.getAbsolutePath());
            }

            try (FileInputStream fis = new FileInputStream(classFile)) {
                // 3. 读取class文件内容
                byte[] data = new byte[(int) classFile.length()];
                int bytesRead = fis.read(data);
                if (bytesRead != data.length) {
                    throw new IOException("Failed to read full class file: " + classFile.getAbsolutePath());
                }

                // 4. 定义类
                Class<?> clazz = defineClass(name, data, 0, data.length);

                // 5. 缓存类
                loadedClasses.put(name, clazz);
                return clazz;
            } catch (IOException e) {
                throw new ClassNotFoundException("Failed to load class: " + name, e);
            }
        }

        /**
         * 获取资源文件的URL。
         *
         * @param name 资源名称
         * @return 资源对应的URL，如果找不到则返回null
         */
        @Override
        public URL getResource(String name) {
            File Dir = new File(classDir);
            if (Dir.exists()) {
                File[] files = Dir.listFiles();
                for (File file : files) {
                    if (file.getName().equals(name)) {
                        try {
                            return file.toURI().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            return super.getResource(name);
        }
    }

    /**
     * 管理类，负责插件的启动、加载和调用。
     */
    public static class Manager {

        /**
         * 加载主类并创建其实例。
         *
         * @param classDir 类文件所在目录
         * @param mainName 主类的全限定名
         * @throws ClassNotFoundException  类未找到异常
         * @throws InstantiationException  实例化异常
         * @throws IllegalAccessException  访问权限异常
         */
        public static void loadMain(String classDir, String mainName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            // 1. 创建新的ClassLoader（每次热更新都创建新实例，指定相同的class目录）
            HotClassLoader newClassLoader = new HotClassLoader(classDir);

            // 2. 设置为线程上下文ClassLoader
            Thread.currentThread().setContextClassLoader(newClassLoader);

            // 3. 加载新版本类（确保所有依赖都通过新ClassLoader加载）
            Class<?> newClass = newClassLoader.loadClass(mainName);

            // 4. 创建新实例
            Object newInstance;
            try {
                newInstance = newClass.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException e) {
                throw new InstantiationException("Failed to instantiate class: " + newClass.getName());
            }

            // 5. 切换到新实例（不替换旧实例，而是替换引用）
            currentInstance = newInstance;
            CLASS_LOADERS.put("current", newClassLoader);
        }

        /**
         * 释放旧的ClassLoader，避免内存泄漏。
         */
        private static void releaseOldClassLoader() {
            // 从存储中移除旧ClassLoader（确保不再被引用）
            CLASS_LOADERS.remove("current");
        }

        /**
         * 调用主类的方法。
         *
         * @throws NoSuchMethodException     方法未找到异常
         * @throws InvocationTargetException 调用目标异常
         * @throws IllegalAccessException    访问权限异常
         */
        public static void callMain(String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            // 通过反射调用方法（确保使用当前ClassLoader加载的类）
            Method method = currentInstance.getClass().getMethod(methodName);
            method.invoke(currentInstance);
        }

        /**
         * 启动插件流程：释放旧ClassLoader、加载主类、调用onEnable方法。
         *
         * @param classDir 类文件所在目录
         * @param mainName 主类的全限定名
         * @throws ClassNotFoundException  类未找到异常
         * @throws InstantiationException  实例化异常
         * @throws IllegalAccessException  访问权限异常
         * @throws InvocationTargetException 调用目标异常
         * @throws NoSuchMethodException     方法未找到异常
         */
        public static void boot(String classDir, String mainName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            releaseOldClassLoader();
            loadMain(classDir, mainName);
            callMain("onEnable");
        }

        /**
         * 从Jar文件启动插件：解压Jar文件到指定目录，然后启动插件。
         *
         * @param file     Jar文件
         * @param classDir 解压后的类文件目录
         * @param mainName 主类的全限定名
         * @throws IOException               IO异常
         * @throws ClassNotFoundException    类未找到异常
         * @throws InvocationTargetException 调用目标异常
         * @throws InstantiationException    实例化异常
         * @throws IllegalAccessException    访问权限异常
         * @throws NoSuchMethodException     方法未找到异常
         */
        public static void bootFromJar(File file, String classDir, String mainName) throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
            File Dir = new File(classDir);
            //清空文件夹
            FileHelper.deleteDir(Dir);
            Dir.mkdirs();

            //解压缩Jar
            FileHelper.unzip(file, Dir);

            boot(classDir, mainName);
        }


        public static void stop() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
            //关闭插件
            callMain("onDisable");
            HandlerList.unregisterAll(BootLoader.getPlugin(BootLoader.class));
            Bukkit.getScheduler().cancelTasks(BootLoader.getPlugin(BootLoader.class));
        }
    }

    /**
     * 文件工具类，提供解压和删除目录的功能。
     */
    public static class FileHelper {

        /**
         * 解压zip文件到指定目录。
         *
         * @param file zip文件
         * @param Dir  解压目录
         * @throws IOException 抛出IO异常
         */
        public static void unzip(File file, File Dir) throws IOException {
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry zipEntry;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    File newFile = new File(Dir, zipEntry.getName());
                    if (!newFile.exists()) {
                        if (zipEntry.isDirectory()) {
                            newFile.mkdirs();
                        } else {
                            newFile.createNewFile();

                            try (FileOutputStream fo = new FileOutputStream(newFile)) {
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = (zis.read(buffer))) != -1) {
                                    fo.write(buffer, 0, length);
                                }
                            }
                        }
                    }
                    zis.closeEntry();
                }
            }
        }

        /**
         * 删除目录及其所有子文件和子目录。
         *
         * @param dir 目标目录
         */
        public static void deleteDir(File dir) {
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteDir(file);
                        } else {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    /**
     * 插件启用时调用，启动插件流程。
     */
    @Override
    public void onEnable() {
        try {
            Manager.bootFromJar(this.getFile(), "./plugins/famara/clazz/", "com.wenkrang.famara.Famara");
        } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            getLogger().log(Level.SEVERE, "Failed to boot Famara plugin", e);
            throw new RuntimeException("Boot failed due to exception", e);
        }
    }

    /**
     * 插件禁用时调用，清理引用以帮助GC回收。
     */
    @Override
    public void onDisable() {
        // 清理引用，帮助GC回收
        currentInstance = null;
        CLASS_LOADERS.clear();
    }
}
