import Line;
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.readLines

class VmParser(inputFie:String) {
    var filePath:String ="";
    var currentIndexLine:Int = 0;
    var fileContents:MutableList<Line> = mutableListOf();
    var numOfLines:Int = 0;

    init {
        this.filePath = inputFie;
        var lineNumber:Int = 0;
        for(line in File(inputFie).readLines()) {
            if(!line.startsWith("//") && line !="")
                this.fileContents.add(Line(line,lineNumber++));
        }
        this.numOfLines = fileContents.size;
    }

    fun parse():List<String>{
        if(currentIndexLine >= numOfLines)
            return emptyList();
        var currentLine:String = fileContents[this.currentIndexLine++].lineContent;
        currentLine.trim('|');
        val parsedLine = currentLine.split(" ");
        return parsedLine;
    }

    fun hasMoreLines():Boolean{
        return this.currentIndexLine < this.numOfLines;
    }

    fun advanceLine(){
        if(hasMoreLines())
            this.currentIndexLine++;
    }

}