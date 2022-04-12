import java.io.File;
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.Path

class VmTranslator {
    fun compile(inputFile:String,outputFile:String=inputFile):Boolean {
        try {
            //initialize the writer
            var outFileName = "${inputFile.removeSuffix(".vm")}/${File(inputFile).nameWithoutExtension}.asm";
            var myWriter:HackWriter = HackWriter(outFileName);

            if(File(outFileName).exists()){ File(outFileName).delete(); }
            File(outFileName).createNewFile();

            //initialize the parser
            var myParser:VmParser = VmParser(inputFile);

            // write hack code for each line
            while (myParser.hasMoreLines()) {
                val cmd:List<String> = myParser.parse();
                myWriter.writeCommand(cmd);
            }
        }
        catch (e:Exception){
            return false;
        }
        return true;
    }

    fun compileProject(inputDirPath:String):Boolean{
        try {
//            Files.walk(Paths.get(inputDirPath)).filter{ it.fileName.endsWith("vm") }.forEach { compile(it.fileName.toString()) }

            File(inputDirPath).walk(FileWalkDirection.BOTTOM_UP).filter { it.name.endsWith("vm")  }.forEach {
                compile(it.name)
            }
            return true;
        }catch (e:Exception){
            return false;
        }
    }

}