import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.io.File;
import java.util.Map;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {
    private static String rootpsd = "0524202471120112211";
    private static Boolean root = false;
    private static String pathDir = System.getProperty("user.dir");
    private static String[] paathDir = pathDir.split("/");
    private static int indexDir = paathDir.length - 1;
    private static String currentDir = paathDir[indexDir];
    private static int modules = 0;
    private static Map<String, Module> loadedModules = new java.util.HashMap<>();

    public static void main(String[] args) {
        System.out.println("""
                
           \n     ███████╗███╗░░██╗████████╗███████╗██████╗░          version: 311225.2r
                ██╔════╝████╗░██║╚══██╔══╝██╔════╝██╔══██╗          modules: """ + modules + """
           \n     █████╗░░██╔██╗██║░░░██║░░░█████╗░░██████╔╝          
                ██╔══╝░░██║╚████║░░░██║░░░██╔══╝░░██╔══██╗          
                ███████╗██║░╚███║░░░██║░░░███████╗██║░░██║
                ╚══════╝╚═╝░░╚══╝░░░╚═╝░░░╚══════╝╚═╝░░╚═╝
                created by arachi  
                """);
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\u001B[32m" + currentDir + "\u001B[0m  >>>>  ");
            String command = sc.nextLine();
            interpretator(command);
        }
    }

    public static void interpretator(String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0) return;

        String cmd = parts[0];

        switch (cmd) {
            case "date":
                LocalDate dt = LocalDate.now();
                System.out.println(dt);
                break;
            case "help":
                System.out.println("""
        ================================
        Enter Terminal 291225.1r by arachi
        ================================
        BASIC:
          help          - show this help
          exit          - quit program
          clear         - clear screen
          enter-version - show version
          echo <text>   - print text
        
        FILE SYSTEM:
          ls            - list files
          pwd           - current directory
          cd <dir>      - change directory
          mkdir <dir>   - create directory
          touch <file>  - create file
          rm <file>     - delete file
          cp <s> <d>    - copy file
          mv <s> <d>    - move/rename file
          cat <file>    - view file
          find <name>   - find files
        
        SYSTEM:
          date          - show date
          time          - show time
          whoami        - current user
          hostname      - system hostname
          ps            - show processes
          kill <pid>    - kill process (simulated)
        
        TEXT:
          grep <p> <f>  - search in file
        
        ADMIN:
          root          - enable root mode
          root-check    - check root status
        
        MODULES:
          module load <jar> <class>  - load module
          module unload <name>       - unload module
          module list                - list modules
        
        ================================
        """);
                break;
            case "time":
                LocalTime tm = LocalTime.now();
                System.out.println(tm);
                break;
            case "mkdir":
                if (parts.length > 1) {
                    mkdirCommand(parts[1]);
                } else {
                    System.out.println("Usage: mkdir <directory>");
                }
                break;

            case "touch":
                if (parts.length > 1) {
                    touchCommand(parts[1]);
                } else {
                    System.out.println("Usage: touch <file>");
                }
                break;

            case "rm":
                if (parts.length > 1) {
                    rmCommand(parts[1]);
                } else {
                    System.out.println("Usage: rm <file>");
                }
                break;

            case "cat":
                if (parts.length > 1) {
                    catCommand(parts[1]);
                } else {
                    System.out.println("Usage: cat <file>");
                }
                break;

            case "cp":
                if (parts.length > 2) {
                    cpCommand(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: cp <source> <destination>");
                }
                break;

            case "mv":
                if (parts.length > 2) {
                    mvCommand(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: mv <source> <destination>");
                }
                break;

            case "whoami":
                System.out.println(System.getProperty("user.name"));
                break;

            case "hostname":
                try {
                    System.out.println(java.net.InetAddress.getLocalHost().getHostName());
                } catch (Exception e) {
                    System.out.println("Unknown host");
                }
                break;

            case "ps":
                psCommand();
                break;

            case "kill":
                if (parts.length > 1) {
                    killCommand(parts[1]);
                } else {
                    System.out.println("Usage: kill <PID>");
                }
                break;

            case "find":
                if (parts.length > 1) {
                    findCommand(parts[1]);
                } else {
                    System.out.println("Usage: find <filename>");
                }
                break;

            case "grep":
                if (parts.length > 2) {
                    grepCommand(parts[1], parts[2]);
                } else {
                    System.out.println("Usage: grep <pattern> <file>");
                }
                break;
            case "root":
                setRoot();
                break;
            case "root-check":
                System.out.println("root " + root);
                break;
            case "exit":
                System.exit(0);
                break;
            case "ls":
                File dir = new File(pathDir);
                String[] files = dir.list();
                if (files != null) {
                    for(String file : files) {
                        System.out.println(file);
                    }
                }
                break;
            case "cd":
                if (parts.length > 1) {
                    changeDir(parts[1]);
                } else {
                    pathDir = System.getProperty("user.home");
                    updateCurrentDir();
                    System.out.println("Now in: " + pathDir);
                }
                break;

            case "echo":
                if (parts.length > 1) {
                    String text = command.substring(5);
                    System.out.println(text);
                }
                break;
            case "pwd":
                System.out.println(pathDir);
                break;
            case "clear":
                for (int i = 0; i < 50; i++) System.out.println();
                break;
            case "enter-version":
                System.out.println("Enter Terminal v281225.1b");
                break;

            case "module":
                handleModuleCommand(parts);
                break;

            case "mem":
                Runtime runtime = Runtime.getRuntime();
                long totalMem = runtime.totalMemory() / (1024 * 1024);
                long freeMem = runtime.freeMemory() / (1024 * 1024);
                long usedMem = totalMem - freeMem;
                System.out.printf("Memory: %d MB used, %d MB free, %d MB total\n", usedMem, freeMem, totalMem);
                break;

            case "disk":
                File disk = new File("/");
                long free = disk.getFreeSpace() / (1024 * 1024 * 1024);
                long total = disk.getTotalSpace() / (1024 * 1024 * 1024);
                System.out.printf("Disk: %d GB free of %d GB\n", free, total);
                break;

            case "uptime":
                long uptime = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
                long hours = uptime / (1000 * 60 * 60);
                long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
                System.out.printf("Uptime: %d hours, %d minutes\n", hours, minutes);
                break;

            case "du":
                String targetDir = parts.length > 1 ? parts[1] : ".";
                File dir_du = new File(targetDir);

                if (!dir_du.exists() || !dir_du.isDirectory()) {
                    System.out.println("Directory not found: " + targetDir);
                    break;
                }

                long size = calculateFolderSize(dir_du);
                System.out.printf("%s: %d KB\n", targetDir, size / 1024);
                break;

            case "env":
                System.getenv().forEach((key, value) -> {
                    System.out.println(key + "=" + value);
                });
                break;



            default:
                Command moduleCommand = findModuleCommand(cmd);
                if (moduleCommand != null) {
                    String[] args = new String[parts.length - 1];
                    System.arraycopy(parts, 1, args, 0, args.length);
                    moduleCommand.execute(args);
                } else {
                    System.out.println("Command not found: " + cmd);
                }
                break;
        }
    }
    private static long calculateFolderSize(File dir) {
        long length = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += calculateFolderSize(file);
                }
            }
        }
        return length;
    }

    private static void handleModuleCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: module [load|unload|list]");
            return;
        }

        String subcmd = parts[1];

        switch (subcmd) {
            case "load":
                if (parts.length == 4) {
                    loadModule(parts[2], parts[3]);
                } else {
                    System.out.println("Usage: module load <jarfile> <ClassName>");
                }
                break;

            case "unload":
                if (parts.length == 3) {
                    unloadModule(parts[2]);
                } else {
                    System.out.println("Usage: module unload <ModuleName>");
                }
                break;

            case "list":
                System.out.println("Loaded modules (" + loadedModules.size() + "):");
                for (String name : loadedModules.keySet()) {
                    Module mod = loadedModules.get(name);
                    System.out.println("  " + name + " v" + mod.getVersion() + " by " + mod.getAuthor());
                }
                break;

            default:
                System.out.println("Unknown module command: " + subcmd);
        }
    }

    private static void loadModule(String jarPath, String className) {
        try {
            System.out.println("DEBUG: Loading " + jarPath + " class " + className);

            File jarFile = new File(jarPath);
            if (!jarFile.isAbsolute()) {
                jarFile = new File(pathDir, jarPath);
            }

            System.out.println("DEBUG: Full path: " + jarFile.getAbsolutePath());

            URL[] urls = {jarFile.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls);

            Class<?> moduleClass = classLoader.loadClass(className);
            Module module = (Module) moduleClass.getDeclaredConstructor().newInstance();

            module.onLoad();
            loadedModules.put(module.getName(), module);
            modules = loadedModules.size();

            System.out.println("✅ Module loaded: " + module.getName() + " v" + module.getVersion());
        } catch (Exception e) {
            System.out.println("❌ Failed to load module: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void unloadModule(String name) {
        if (loadedModules.containsKey(name)) {
            loadedModules.get(name).onUnload();
            loadedModules.remove(name);
            modules = loadedModules.size();
            System.out.println("✅ Module unloaded: " + name);
        } else {
            System.out.println("Module not found: " + name);
        }
    }

    private static Command findModuleCommand(String commandName) {
        for (Module module : loadedModules.values()) {
            Map<String, Command> commands = module.getCommands();
            if (commands.containsKey(commandName)) {
                return commands.get(commandName);
            }
        }
        return null;
    }

    private static void updateCurrentDir() {
        File file = new File(pathDir);
        String name = file.getName();
        currentDir = name.isEmpty() ? "/" : name;
    }

    public static void changeDir(String path) {
        File newDir = new File(pathDir, path);

        if (newDir.exists() && newDir.isDirectory()) {
            try {
                pathDir = newDir.getCanonicalPath();
            } catch (Exception e) {
                pathDir = newDir.getAbsolutePath();
            }

            File currentFile = new File(pathDir);
            currentDir = currentFile.getName();
            if (currentDir.isEmpty()) currentDir = "/";

            System.out.println("now in: " + pathDir);
        } else {
            System.out.println("Error 404: Directory not found: " + path);
        }
    }

    private static void mkdirCommand(String dirName) {
        File newDir = new File(pathDir, dirName);
        if (newDir.mkdir()) {
            System.out.println("Directory created: " + dirName);
        } else {
            System.out.println("Failed to create directory");
        }
    }

    private static void touchCommand(String fileName) {
        try {
            File file = new File(pathDir, fileName);
            if (file.createNewFile()) {
                System.out.println("File created: " + fileName);
            } else {
                System.out.println("File already exists");
            }
        } catch (Exception e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    private static void rmCommand(String target) {
        File file = new File(pathDir, target);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Deleted: " + target);
            } else {
                System.out.println("Failed to delete");
            }
        } else {
            System.out.println("File not found: " + target);
        }
    }

    private static void catCommand(String fileName) {
        try {
            File file = new File(pathDir, fileName);
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                System.out.println(fileScanner.nextLine());
            }
            fileScanner.close();
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static void cpCommand(String source, String dest) {
        try {
            File srcFile = new File(pathDir, source);
            File dstFile = new File(pathDir, dest);

            java.nio.file.Files.copy(
                    srcFile.toPath(),
                    dstFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            System.out.println("Copied: " + source + " → " + dest);
        } catch (Exception e) {
            System.out.println("Error copying: " + e.getMessage());
        }
    }

    private static void mvCommand(String source, String dest) {
        File srcFile = new File(pathDir, source);
        File dstFile = new File(pathDir, dest);

        if (srcFile.renameTo(dstFile)) {
            System.out.println("Moved: " + source + " → " + dest);
        } else {
            System.out.println("Failed to move");
        }
    }

    private static void psCommand() {
        java.lang.management.RuntimeMXBean runtime =
                java.lang.management.ManagementFactory.getRuntimeMXBean();
        System.out.println("PID: " + runtime.getName().split("@")[0]);
        System.out.println("JVM: " + System.getProperty("java.version"));
    }

    private static void killCommand(String pidStr) {
        System.out.println("(Simulated) Killing process: " + pidStr);
    }

    private static void findCommand(String pattern) {
        File dir = new File(pathDir);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(pattern)) {
                    System.out.println(file.getPath());
                }
            }
        }
    }

    private static void grepCommand(String pattern, String fileName) {
        try {
            File file = new File(pathDir, fileName);
            Scanner scanner = new Scanner(file);
            int lineNum = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(pattern)) {
                    System.out.println(lineNum + ": " + line);
                }
                lineNum++;
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void setRoot() {
        Scanner sc = new Scanner(System.in);
        System.out.print("(enter) root passwd: ");
        String passwd_us = sc.nextLine();
        if (passwd_us.equals(rootpsd)) {
            root = true;
            System.out.println("Root enable");
        } else {
            System.out.println("Error: 072");
        }
    }
}