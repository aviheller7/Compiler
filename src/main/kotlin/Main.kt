import VmTranslator

fun main(args: Array<String>) {
    println("------- Welcome to VM to Assembler Compiler! -------")
    while (true){
        println("Do You Want To Compile a project? (Yes/No):")
        if("yes" != readln().trim()) {println("Goodbye"); return;}
        println("Please Enter Projects' Directory Path You Want To Compile:")
        val dirPath:String = readln()
        println("Compiling...")
        val translator:VmTranslator = VmTranslator();
        if(translator.compileProject(dirPath)) println("Done")
        else println("Error!")
    }
}