package fr.thesmyler.terracleanup;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

import fr.thesmyler.terracleanup.elevation.ElevationClassification;
import fr.thesmyler.terracleanup.elevation.ElevationClassifier;
import fr.thesmyler.terracleanup.util.Formatting;
import fr.thesmyler.terracleanup.util.coordinates.Region3dColumn;
import fr.thesmyler.terracleanup.util.coordinates.Region3dPosition;
import net.buildtheearth.terraminusminus.TerraConfig;
import net.buildtheearth.terraminusminus.TerraConstants;
import net.buildtheearth.terraminusminus.TerraMinusMinus;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;

public class TerraCleanupHeadless {
	
	public static void main(String... args) throws IOException, InterruptedException {

		Options options = makeOptions();
		
        CommandLine cmd = null;

        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp(args[0], options);
            System.exit(1);
        }
        
        if(cmd.hasOption(OPTION_QUIET)) setPrintingQuiet();
        else if(cmd.hasOption(OPTION_VERBOSE)) setPrintingVerbose();
        else setPrintingNormal();
        
        if(cmd.hasOption(OPTION_INFO)) {
        	printInfo();
        	System.exit(0);
        }
		
		int above = 500;
		int below = 500;
		
		if(cmd.hasOption(OPTION_SURFACE_ABOVE)) {
			try {
				above = Integer.parseInt(cmd.getOptionValue(OPTION_SURFACE_ABOVE));
			} catch(NumberFormatException e) {
				System.err.println("Invalid value for above surface option");
				System.exit(1);
			}
		}
		
		if(cmd.hasOption(OPTION_SURFACE_UNDER)) {
			try {
				below = Integer.parseInt(cmd.getOptionValue(OPTION_SURFACE_UNDER));
			} catch(NumberFormatException e) {
				System.err.println("Invalid value for below surface option");
				System.exit(1);
			}
		}
				
		int processors = Runtime.getRuntime().availableProcessors();
		if(cmd.hasOption(OPTION_THREADS)) {
			try {
				below = Integer.parseInt(cmd.getOptionValue(OPTION_THREADS));
				if(below < 1) throw new NumberFormatException();
			} catch(NumberFormatException e) {
				System.err.println("Invalid number of thread specifed");
				System.exit(1);
			}
		}

		EarthGeneratorSettings settings = EarthGeneratorSettings.parse(EarthGeneratorSettings.DEFAULT_SETTINGS);
		if(cmd.hasOption(OPTION_WORLD_SETTINGS) && cmd.hasOption(OPTION_WORLD_SETTINGS_BTE)) {
			System.err.println("Cannot use both BTE world settings and a custom settings");
			System.exit(1);
		} else if(cmd.hasOption(OPTION_WORLD_SETTINGS_BTE)) {
			settings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
		} else if(cmd.hasOption(OPTION_WORLD_SETTINGS)) {
			settings = EarthGeneratorSettings.parse(cmd.getOptionValue(OPTION_WORLD_SETTINGS));
		}
		
		int sampling = 8;
		if(cmd.hasOption(OPTION_THREADS)) {
			try {
				sampling = Integer.parseInt(cmd.getOptionValue(OPTION_THREADS));
				if(sampling < 1) throw new NumberFormatException();
			} catch(NumberFormatException e) {
				System.err.println("Invalid sampling step value specified");
				System.exit(1);
			}
		}
		
		ElevationClassifier<Region3dColumn, Region3dPosition> classifier = new ElevationClassifier<>(settings, 8, below, above, processors);
		
		File region3dFolder = new File(cmd.getOptionValue(OPTION_WORLD)).toPath().resolve("region3d").toFile();
		if(!region3dFolder.exists() || !region3dFolder.isDirectory()) {
			System.err.println(region3dFolder.getPath() + "is not a valid region3d folder");
			System.exit(1);
		}
		List<Path> paths = new ArrayList<>();
		try {
			Files.list(region3dFolder.toPath()).forEach(p -> paths.add(p));
			System.out.println(String.format("Found %d files", paths.size()));
		} catch(NoSuchFileException e) {
			System.err.println(region3dFolder.getPath() + "is not a valid region3d folder");
		}
		
		Region3dCleanupReport report = new Region3dCleanupReport();
		
		for(Path p: paths) {
			try {
				Region3dPosition pos = new Region3dPosition(p);
				classifier.classify(pos).thenAccept(c -> report.addPath(p, c));
			} catch(Throwable  t) {
				System.out.println(String.format("Ignoring %s", p));
			}
		}
		
		while(classifier.queueSize() > 0) {
			System.out.print(String.format("Processing... (%d remaining) | Cache size: %d | Available memory: %s | Press enter to show progress\r", classifier.queueSize(), classifier.cacheCount(), Formatting.humanReadableByteCountBin(Runtime.getRuntime().freeMemory())));
			if(System.in.available() > 0) {
				while(System.in.available() > 0) System.in.read();
				System.out.println();
				for(ElevationClassification c: ElevationClassification.values()) {
					System.out.println(String.format("%s: %d (%s)", c, report.getCount(c),  Formatting.humanReadableByteCountBin(report.getSize(c))));
				}
				System.out.println();
				System.out.flush();
			}
			Thread.sleep(100);
		}
		
		PrintStream out = System.out;
		if(cmd.hasOption(OPTION_REPORT)) {
			File reportFile = new File(cmd.getOptionValue(OPTION_REPORT));
			reportFile.createNewFile();
			out = new PrintStream(reportFile);
		}
		System.out.println();
		classifier.terminate();
		
		report.printTo(out);
		out.close();
		System.exit(0);
		
	}
	
	private static final String OPTION_VERBOSE = "v";
	private static final String OPTION_QUIET = "q";
	private static final String OPTION_INFO = "i";
	private static final String OPTION_SURFACE_ABOVE = "sa";
	private static final String OPTION_SURFACE_UNDER = "su";
	private static final String OPTION_THREADS = "t";
	private static final String OPTION_WORLD_SETTINGS = "s";
	private static final String OPTION_WORLD_SETTINGS_BTE = "bte";
	private static final String OPTION_WORLD = "w";
	private static final String OPTION_SAMPLE_STEP = "ss";
	private static final String OPTION_REPORT = "r";
	
	public static Options makeOptions() {
		Options options = new Options();
		options.addOption(
				Option.builder(OPTION_VERBOSE)
				.longOpt("verbose")
				.desc("print out web requests etc")
				.required(false)
				.hasArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_QUIET)
				.longOpt("quiet")
				.desc("silence all output")
				.required(false)
				.hasArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_INFO)
				.longOpt("info")
				.desc("print software information and exit")
				.required(false)
				.hasArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_SURFACE_ABOVE)
				.longOpt("surface-above")
				.desc("how far above ground level should regions be kept, in meters (default is 500)")
				.required(false)
				.hasArg(true)
				.optionalArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_SURFACE_UNDER)
				.longOpt("surface-below")
				.desc("how far below ground level should regions be kept, in meters (default is 500)")
				.required(false)
				.hasArg(true)
				.optionalArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_THREADS)
				.longOpt("threads")
				.desc("number of threads to use")
				.required(false)
				.hasArg(true)
				.optionalArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_WORLD_SETTINGS)
				.longOpt("world-settings")
				.desc("world json settings")
				.required(false)
				.hasArg(true)
				.optionalArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_WORLD_SETTINGS_BTE)
				.longOpt("build-the-earth")
				.desc("use the BTE world settings (incompatible with -s)")
				.required(false)
				.hasArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_WORLD)
				.longOpt("world")
				.desc("world directory to work on (mandatory)")
				.required()
				.hasArg(true)
				.optionalArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_SAMPLE_STEP)
				.longOpt("sampling-step")
				.desc("interval to sample elevation at, in blocks")
				.required(false)
				.hasArg(true)
				.optionalArg(false)
			.build());
		options.addOption(
				Option.builder(OPTION_REPORT)
				.longOpt("report-file")
				.desc("file path to write a report at, if this is not specified, the report will be printed to the console")
				.required(false)
				.hasArg(true)
				.optionalArg(false)
			.build());
		return options;
	}
	
	private static void setPrintingQuiet() {
		OutputStream devnull = new OutputStream() {
			@Override
			public void write(int arg0) throws IOException {}
		};
		System.setOut(new PrintStream(devnull));
		System.setErr(new PrintStream(devnull));
		TerraMinusMinus.LOGGER = new SimpleLogger("terra--", Level.OFF, false, false, false, false, null, null, new PropertiesUtil("log4j2.simplelog.properties"), System.out);
		TerraConfig.reducedConsoleMessages = true;
	}
	
	private static void setPrintingVerbose() {
		TerraMinusMinus.LOGGER = new SimpleLogger("terra--", Level.ALL, false, false, false, false, null, null, new PropertiesUtil("log4j2.simplelog.properties"), System.out);
		TerraConfig.reducedConsoleMessages = false;
	}

	private static void setPrintingNormal() {
		TerraMinusMinus.LOGGER = new SimpleLogger("terra--", Level.ERROR, false, false, false, false, null, null, new PropertiesUtil("log4j2.simplelog.properties"), System.out);
		TerraConfig.reducedConsoleMessages = true;
	}
	
	private static void printInfo() {
		System.out.println(String.format("TerraCleanup version %s", TerraCleanupConstants.VERSION));
		System.out.println(String.format("Terra-- version %s", TerraConstants.VERSION));
	}
}
