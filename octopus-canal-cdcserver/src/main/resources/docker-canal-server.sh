#NORMAL
docker run -d -it -h 192.168.23.227 --name=canal-server -p 11110:11110 -p 11111:11111 -p 11112:11112 -p 9100:9100 \
-e canal.destinations=test \
-e canal.instance.master.address=192.168.23.227:3306 \
-e canal.instance.dbUsername=canal \
-e canal.instance.dbPassword=canal \
-e canal.instance.connectionCharset=UTF-8 \
-e canal.instance.tsdb.enable=true \
-e canal.instance.gtidon=false \
-e canal.instance.filter.regex=orders\\.global_events \
canal/canal-server:v1.1.4


#MQ
docker run -d -it -h 192.168.23.227 --name=mq-canal-server -p 12110:11110 -p 12111:11111 -p 12112:11112 -p 9200:9100 \
-e canal.instance.master.address=192.168.23.227:3306 \
-e canal.instance.dbUsername=canal \
-e canal.instance.dbPassword=canal \
-e canal.instance.connectionCharset=UTF-8 \
-e canal.instance.tsdb.enable=true \
-e canal.instance.gtidon=false \
-e canal.serverMode=kafka \
-e canal.mq.servers=192.168.23.227:9092 \
-e canal.mq.flatMessage=true \
-e canal.mq.topic=octopus_global_events \
-e canal.mq.partitionsNum=3 \
-e canal.mq.partitionHash=orders\\.global_events:id \
-e canal.instance.filter.regex=orders\\.global_events \
canal/canal-server:v1.1.4
