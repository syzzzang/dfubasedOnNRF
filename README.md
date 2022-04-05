# nRF Toolbox


새로운 펌웨어 파일을 추가하려면

1. app\src\main\assets에 있는 기존 파일을 삭제


![image](https://user-images.githubusercontent.com/61811705/91026500-c6baaf80-e635-11ea-881c-869e1a9b0beb.png)

2. DfuActivity.java에 276 라인 
    copyAsset("펌웨어파일명.zip");
   
----------------------------------------------------------------------------