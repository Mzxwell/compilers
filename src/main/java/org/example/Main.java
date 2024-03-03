package org.example;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        //初始化LLVM
        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();
        //创建module
        LLVMModuleRef module = LLVMModuleCreateWithName("module");

        //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
        LLVMBuilderRef builder = LLVMCreateBuilder();

        //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
        LLVMTypeRef i32Type = LLVMInt32Type();
        //创建一个常量,这里是常数0
        LLVMValueRef zero = LLVMConstInt(i32Type, 0, /* signExtend */ 0);

        //创建名为globalVar的全局变量
        LLVMValueRef globalVar = LLVMAddGlobal(module, i32Type, /*globalVarName:String*/"globalVar");

        //为全局变量设置初始化器
        LLVMSetInitializer(globalVar, /* constantVal:LLVMValueRef*/zero);
        //生成返回值类型
        LLVMTypeRef returnType = i32Type;

        //生成函数参数类型
        PointerPointer<Pointer> argumentTypes = new PointerPointer<>(2)
                .put(0, i32Type)
                .put(1, i32Type);

        //生成函数类型
        LLVMTypeRef ft = LLVMFunctionType(returnType, argumentTypes, /* argumentCount */ 2, /* isVariadic */ 0);
        //若仅需一个参数也可以使用如下方式直接生成函数类型
        ft = LLVMFunctionType(returnType, i32Type, /* argumentCount */ 1, /* isVariadic */ 0);

        //生成函数，即向之前创建的module中添加函数
        LLVMValueRef function = LLVMAddFunction(module, /*functionName:String*/"function", ft);

        //通过如下语句在函数中加入基本块，一个函数可以加入多个基本块
        LLVMBasicBlockRef block1 = LLVMAppendBasicBlock(function, /*blockName:String*/"block1");

        LLVMBasicBlockRef block2 = LLVMAppendBasicBlock(function, /*blockName:String*/"block2");

        //选择要在哪个基本块后追加指令
        LLVMPositionBuilderAtEnd(builder, block1);//后续生成的指令将追加在block1的后面

        //获取函数的参数
        LLVMValueRef n = LLVMGetParam(function, /* parameterIndex */0);

        //创建add指令并将结果保存在一个临时变量中
        LLVMValueRef result = LLVMBuildAdd(builder, n, zero, /* varName:String */"result");

        //跳转指令决定跳转到哪个块
        LLVMBuildBr(builder, block2);

        //生成比较指令
        LLVMValueRef condition = LLVMBuildICmp(builder, /*这是个int型常量，表示比较的方式*/LLVMIntEQ, n, zero, "condition = n == 0");
        LLVMBasicBlockRef ifTrue= LLVMAppendBasicBlock(function,"true");
        LLVMBasicBlockRef ifFalse= LLVMAppendBasicBlock(function,"false");
    /* 上面参数中的常量包含如下取值
        LLVMIntEQ,
        LLVMIntNE,
        LLVMIntUGT,
        LLVMIntUGE,
        LLVMIntULT,
        LLVMIntULE,
        LLVMIntSGT,
        LLVMIntSGE,
        LLVMIntSLT,
        LLVMIntSLE,
    */
        //条件跳转指令，选择跳转到哪个块
        LLVMBuildCondBr(builder,
                /*condition:LLVMValueRef*/ condition,
                /*ifTrue:LLVMBasicBlockRef*/ ifTrue,
                /*ifFalse:LLVMBasicBlockRef*/ ifFalse);
        LLVMValueRef tmp_=LLVMBuildICmp(builder, /*这是个int型常量，表示比较的方式*/LLVMIntEQ, n, zero, "condition = n == 0");
        tmp_ = LLVMBuildICmp(builder, LLVMIntNE, LLVMConstInt(i32Type, 0, 0), tmp_, "tmp_");
// 生成xor
        tmp_ = LLVMBuildXor(builder, tmp_, LLVMConstInt(LLVMInt1Type(), 1, 0), "tmp_");
// 生成zext
        tmp_ = LLVMBuildZExt(builder, tmp_, i32Type, "tmp_");
        LLVMPositionBuilderAtEnd(builder, block2);//后续生成的指令将追加在block2的后面

        //函数返回指令
        LLVMBuildRet(builder, /*result:LLVMValueRef*/result);
        LLVMPositionBuilderAtEnd(builder, block1);

        //int型变量
        //申请一块能存放int型的内存
        LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/"pointer");

        //将数值存入该内存
        LLVMBuildStore(builder, zero, pointer);

        //从内存中将值取出
        LLVMValueRef value = LLVMBuildLoad(builder, pointer, /*varName:String*/"value");


        //数组变量
        //创建可存放200个int的vector类型
        LLVMTypeRef vectorType = LLVMVectorType(i32Type, 200);

        //申请一个可存放该vector类型的内存
        LLVMValueRef vectorPointer = LLVMBuildAlloca(builder, vectorType, "vectorPointor");
        // 生成icmp
        // 生成icmp



        LLVMDumpModule(module);
        final BytePointer error = new BytePointer();
        LLVMPrintModuleToFile(module,"test.ll",error);

    }
}