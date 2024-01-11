# 인스턴스에 클론 받은 디렉토리 이름을 `app`으로 바꿔야합니다.
APPLICATION_PATH=/home/ubuntu/app
# shellcheck disable=SC2164
cd $APPLICATION_PATH

# shellcheck disable=SC2010
JAR_NAME=$(ls $APPLICATION_PATH/build/libs/ | grep '.jar' | tail -n 1)

# shellcheck disable=SC2034
JAR_PATH=build/libs/$JAR_NAME
JAR_PID=$(pgrep -f $JAR_NAME)

 if [ -z $JAR_PID ]
 then
   echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
 else
   echo "> sudo kill -15 $JAR_PID"
   sudo kill -15 $JAR_PID
   sleep 10
 fi

 echo "> $JAR_PATH 배포" #3
 # shellcheck disable=SC2153
 # shellcheck disable=SC2024
 source ~/.bashrc
 sudo nohup java -jar -Dspring.profiles.active=prod "$JAR_PATH" >nohup.out 2>&1 </dev/null &