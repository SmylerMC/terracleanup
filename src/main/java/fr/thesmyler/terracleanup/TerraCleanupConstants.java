package fr.thesmyler.terracleanup;

import java.io.InputStream;
import java.util.Scanner;

public class TerraCleanupConstants {
	
	public static final String VERSION;
	
	static {
		String version = "error";
		try(
				InputStream in = TerraCleanupConstants.class.getResourceAsStream("/version.properties");
				Scanner sc = new Scanner(in);
			) {
			version = sc.nextLine();
		} catch (Throwable e) {
			System.err.println("Failed to load version!");
			e.printStackTrace();
		}
		VERSION = version;
	}

}
