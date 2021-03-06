package edu.buffalo.cse.blueseal.BSFlow;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import edu.buffalo.cse.blueseal.blueseal.Constants;

import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.Transform;

public class InterProceduralMain {
	public final static PrintStream ps = System.out;
	public static void main(String[] args) throws FileNotFoundException {
		if(args.length == 0){
		  System.err.print("Missing apk path. Exit!\n");
		  System.exit(1);
		}
		String apkpath = args[0];
		System.out.println("Analyzing:"+apkpath);
		//redirect the stdout to get rid of Soot output info
		//System.setOut(new PrintStream("/dev/null"));
		//Get the sources and sinks from the input files
		SourceSink.extractSootSourceSink();
		
		/*
		 * this part is for Jimple
		 */
		//the following transform modifies the callgraph
		{
			SceneTransformer cgTransformer = new CgTransformer(args[0]);
			Pack pack = PackManager.v().getPack("cg");
			pack.add(new Transform("cg.mtran", cgTransformer));
		
			PackManager.v().getPack("wjtp").
				add(new Transform("wjtp.inter", BSInterproceduralTransformer.v()));
	
			String[] sootArgs = {"-w","-f", "n", "-allow-phantom-refs", "-x",
									"android.support.", "-x", "android.annotation.", 
									"-process-dir", args[0],
									"-android-jars", Constants.ANDROID_JARS, 
									"-src-prec", "apk",
									"-no-bodies-for-excluded"
//									"-dump-cfg", "cg.mtran"
									};
			//add the following class to solve a CHATransform exception
			//TODO: if this is the only way, create a separate file to add all basic classes
//			Scene.v().addBasicClass("android.support.v4.widget.DrawerLayout",SootClass.BODIES);
//			Scene.v().addBasicClass("org.apache.http.client.utils.URLEncodedUtils",SootClass.SIGNATURES);
//			Scene.v().addBasicClass("org.apache.http.protocol.BasicHttpContext",SootClass.HIERARCHY);
			
			soot.Main.main(sootArgs);
		}
		
		
//		/*testing shimple*/
//		{
//			SceneTransformer cgTransformer = new CgTransformer(args[0]);
//			
//			PackManager.v().getPack("cg")
//				.add(new Transform("cg.mtran", cgTransformer));
//			PackManager.v().getPack("wstp")
//				.add(new Transform("wstp.inter", BSInterproceduralTransformer.v()));
//	 
//
//			String[] sootArgs = {"-w","-ws","-f", "S", "-allow-phantom-refs", "-x",
//									"android.support.", "-x", "android.annotation.", 
//									"-process-dir", args[0],
//									"-android-jars", Constants.ANDROID_JARS, 
//									"-src-prec", "apk"};
//			soot.Main.main(sootArgs);
//		}

		
		

  }
  
  public static void println(Object o){
	  ps.println(o);
	  
  }

}
