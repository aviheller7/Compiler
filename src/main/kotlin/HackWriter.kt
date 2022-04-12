import java.io.File

class HackWriter(outputFile:String) {

    var filePath:String = "";

    var lableNumRun = 0;

    var segments = mapOf("local" to "LCL", "argument" to "ARG", "this" to "THIS","that" to "THAT")

    init {
        this.filePath = outputFile;
        asmWriter(stackInitCommand());
    }

    fun writeCommand(cmd:List<String>){
        when(cmd[0]){
            "pop","push" -> writeMemoryAccess(Commands.valueOf(cmd[0]),cmd[1],cmd[2].toInt())
            "add","sub","neg","gt","eq","ge",
            "ne","le","and","or","not" -> writeAritmetic(Commands.valueOf(cmd[0]))
        }
    }

    fun writeAritmetic(cmd:Commands){
        when(cmd){
            Commands.add -> asmWriter(arithmaticBinaryCommand("+"));
            Commands.sub -> asmWriter(arithmaticBinaryCommand("-"));
            Commands.neg -> asmWriter(arithmaticUnaryCommand("!"));

            Commands.gt -> asmWriter(compareCommand("GT"));
            Commands.eq -> asmWriter(compareCommand("EQ"));
            Commands.ge -> asmWriter(compareCommand("GE"));
            Commands.ne -> asmWriter(compareCommand("NE"));
            Commands.le -> asmWriter(compareCommand("LE"));

            Commands.and -> asmWriter(arithmaticBinaryCommand("&"));
            Commands.or -> asmWriter(arithmaticBinaryCommand("||"));
            Commands.not -> asmWriter(arithmaticUnaryCommand("!"));
        }
    }

    fun writeMemoryAccess(cmd:Commands, segment:String,index:Int){
        when(cmd){
            Commands.pop -> when(segment){
                "local","local", "argument", "this","that"-> asmWriter(popSegment(segments[segment].toString(),index))
                "temp" -> asmWriter(popTemp(index))
                "static" -> asmWriter(popStatic(index))
                "pointer" -> asmWriter(popPointer(index + 3))
            }
            Commands.push -> when(segment){
                "local","local", "argument", "this","that"-> asmWriter(pushSegment(segments[segment].toString(),index))
                "temp" -> asmWriter(pushTemp(index))
                "static" -> asmWriter(pushStatic(index))
                "pointer" -> asmWriter(pushPointer(index + 3))
                "constant" -> asmWriter(pushConstant(index))
            }
        }
    }

    fun stackInitCommand(): String {
        return ("""
            //stackInitCommand
            @256
            D=A
            @SP
            M=D
            """.trimIndent())
    }

    fun arithmaticUnaryCommand(unaryOp:String):String{
        var asmCode:String ="""
            @SP
            A=M-1
            M = ${unaryOp}M 
            @SP
            M=M-1
        """.trimIndent()
        return asmCode;
    }

    fun arithmaticBinaryCommand(binaryOp:String):String{
        var asmCode:String ="""
            @SP 
            A = M-1
            D = M 
            A = A-1 
            M = M${binaryOp}D
            @SP
            M=M-1
        """.trimIndent()
        return asmCode;
    }

    fun compareCommand(compareOp:String):String{
        var asmCode:String ="""
            @SP
            A=M-1
            D = M
            @SP 
            A=M-1 
            A=A-1 
            D = D - M 
            @IF_TRUE${lableNumRun++}
            D:J${compareOp}
            D=0 
            @SP 
            A=M-1 
            A=A-1 
            M = D 
            @IF_FALSE${lableNumRun}
            0;JMP 
            (IF_TRUE${lableNumRun})
            D=-1
            @SP 
            A=M-1
            A=A-1
            M=D 
            IF_FALSE${lableNumRun}
            @SP 
            M=M-1
        """.trimIndent()
        return asmCode;
    }

    fun pushSegment(segment:String, num:Int):String{
        val asmCode:String = """
            @${segment}
            D = M
            @${num}
            A = A+D
            D = M 
            @SP 
            A = M 
            M = D 
            @SP
            M=M+1
        """.trimIndent()
        return asmCode;
    }

    fun popSegment(segment:String, num:Int):String{
        val asmCode:String ="""
            @${segment}
            D = M 
            @${num} 
            D = A+D 
            @13 
            M = D 
            @SP
            A = M-1        
            D = M
            @13
            A = M
            M = D
            @SP
            M=M-1
            """.trimIndent()
        return asmCode;
    }

    fun popTemp(num:Int):String{
        val asmCode:String = """
            @SP 
            A = M-1
            D = M
            @${num+5} 
            M=D 
            @SP 
            M=M-1
        """.trimIndent()
        return asmCode;
    }

    fun pushTemp(num:Int):String{
        val asmCode:String = """
            @${num+5} 
            D = M
            @SP
            A = M
            M=D
            @SP
            M=M+1
        """.trimIndent()
        return asmCode;
    }

    fun popStatic(num:Int):String{
        val asmCode:String =
            "@SP \n" +
            "A = M-1 \n" +
            "D = M\n" +
            "@ClassName.${num} \n" +
            "M=D \n" +
            "@SP \n" +
            "M=M-1 \n";
        return asmCode;
    }

    fun pushStatic(num:Int):String{
        val asmCode:String =
            "@ClassName.${num} \n" +
            "D = M\n" +
            "@SP \n" +
            "A = M \n" +
            "M = D \n" +
            "@SP \n" +
            "M=M+1 \n";
        return asmCode;
    }

    fun pushConstant(num:Int):String{
        val asmCode:String = """
            @${num}
            D=A
            @SP
            A = M 
            M = D
            @SP 
            M=M+1 """.trimIndent()
        return asmCode;
    }

    fun popPointer(thisOrThat:Int):String{
        val asmCode:String = """
            @SP 
            A = M-1
            D = M
            @${thisOrThat} 
            M=D 
            @SP 
            M=M-1
        """.trimIndent()
        return asmCode;

    }

    fun pushPointer(thisOrThat:Int):String {
        val asmCode:String = """
            @${thisOrThat} 
            D = M

            @SP 
            A = M
            M = D

            @SP 
            M=M+1
        """.trimIndent()
        return asmCode;

    }

    fun asmWriter(asmCode:String){
        File(this.filePath).appendText("\n$asmCode\n");
    }

}