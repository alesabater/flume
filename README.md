flume
=====

flume.rabbitmq.conf > consist of a configuration file to ingest data in a hadoop cluster using a rabbitmq server as source.

flume.netcat.conf > consist of a configuration file to ingest data in a hadoop cluster using linux netcat service as source.

GTRSerializer > is a UDF serializer for flume. It matches the data you are interested in based on a regex pattern, if there are many fields inside that match, you can specify a delimiter to split it.
