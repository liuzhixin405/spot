create database btcside character set utf8mb4 collate utf8mb4_general_ci;
create user 'btcside'@'%' identified by '(btcside@Wf8t)-U2Yw';
grant all on btcside.* to btcside@'%';