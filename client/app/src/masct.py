#encoding= 'utf8'
import sys, shutil, os, string, codecs
import os.path

outputConfigs = {
    "app_js_part" : {
        "annotationFormat": "//",
        "fileExtensions" : { "java","xml" },
        "outputFileName" : ".\\app_source_code.txt",
        "sourceDirNames" : { 
            ".\\main"
        },
        "ignores":{}
    },
   
   "app_android_part" : {
        "annotationFormat": "//",
        "fileExtensions" : { "java", "h", "cpp", "c", "hpp" },
        "outputFileName" : ".\\app_android_part.txt",
        "sourceDirNames" : { 
            ".\\android"
        },
        "ignores": { 
            ".\\android\\build\\"
        }
    },
    "app_ios_part": {
        #注释格式
        "annotationFormat": "//",
        
        #这是目标文件后缀 数组
        "fileExtensions" : { "m", "h", "cpp", "c", "hpp" }, 
        
        #目标文件目录 数组
        "sourceDirNames" : { ".\\ios" },
        
        #输出至文件
        "outputFileName" : ".\\app_ios_part.txt", 

        #忽略
        "ignores": { } 
    } 
    
}

if __name__ == '__main__':
    for key in outputConfigs:
        sourceDirNames = outputConfigs[key]["sourceDirNames"]
        fileExtensions = outputConfigs[key]["fileExtensions"]
        outputFileName = outputConfigs[key]["outputFileName"]
        annotationFormat = outputConfigs[key]["annotationFormat"]
        ignores = outputConfigs[key]["ignores"]
        try:
            output = open(outputFileName, 'w', encoding='utf8')
            output.write("")

        finally:
            output.close()
        
        try:
            output = open(outputFileName, 'w+', encoding='utf8')
        except e:
            output.close()
            print("Fail to open file:"+outputFileName)
            continue
            
        for value in sourceDirNames:
            for parent,dirname,filenames in os.walk(value):
                isIgnore = False

                for i in ignores:
                    if not parent.find(i) == -1:
                        isIgnore = True
                        break;
                        
                if isIgnore:
                        print("ignore directory: "+parent)
                        continue
                        
                for filename in filenames:
                
                    isIgnore = False
                    
                    for i in ignores:
                        if not filename.find(i) == -1:
                            isIgnore = True
                            break;
                    
                    if isIgnore:
                        print("ignore file: "+parent+"\\"+filename)
                        continue
                        
                    isMatch = False
                    fileNameLen = len(filename)
                    
                    tempArr = filename.split(".")

                    if len(tempArr) < 2:
                        continue
                    
                    fileExtension = tempArr[len(tempArr)-1]
                    
                    for targetExtension in fileExtensions:
                        if fileExtension == targetExtension:
                            isMatch = True
                            break
                    
                    if isMatch:
                        output.write(annotationFormat+' Extension: '+ fileExtension +'\n')
                        output.write(annotationFormat+' Directory: '+ parent +'\n')
                        output.write(annotationFormat+' File Name: '+ filename +'\n')
                        
                        try:
                            codeFile = open(parent+'\\'+filename, 'r',encoding='utf8')
                            output.write(codeFile.read())

                        finally:
                            codeFile.close()
            
                        output.write('\n')
                        
        output.close()