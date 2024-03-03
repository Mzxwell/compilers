; ModuleID = 'module'
source_filename = "module"

@globalVar = global i32 0

define i32 @function(i32 %0) {
block1:
  %result = add i32 %0, 0
  br label %block2
  %"condition = n == 0" = icmp eq i32 %0, 0
  br i1 %"condition = n == 0", label %true, label %false
  %pointer = alloca i32, align 4
  store i32 0, i32* %pointer, align 4
  %value = load i32, i32* %pointer, align 4
  %vectorPointor = alloca <200 x i32>, align 1024

block2:                                           ; preds = %block1
  ret i32 %result

true:                                             ; preds = %block1

false:                                            ; preds = %block1
}
