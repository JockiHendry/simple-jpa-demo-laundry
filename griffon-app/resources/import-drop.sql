alter table EventPekerjaan drop foreign key FK_qonm5ahrj2kxc4ct2jlskby2c
alter table ItemPakaian drop foreign key FK_qhibu36etmaf6efsp6hetswq5
alter table ItemWorkOrder drop foreign key FK_qidm6j27256q8x9th8r27q43b
alter table ItemWorkOrder drop foreign key FK_jsocwnor4t29jaqky7jw75u8j
alter table Work drop foreign key FK_q3gdl83u4jkkl3o3qdjggqoe9
alter table Work drop foreign key FK_c2xf386v4jfxp5hho4rcg06tl
alter table WorkOrder drop foreign key FK_fr5t4jc9nqok0k8fpt2uy5xsp
alter table WorkOrder drop foreign key FK_2kmvja4i7gcsysw2s2m3kf8n8
drop table if exists EventPekerjaan
drop table if exists ItemPakaian
drop table if exists ItemWorkOrder
drop table if exists JenisWork
drop table if exists Kategori
drop table if exists Pelanggan
drop table if exists Pembayaran
drop table if exists Work
drop table if exists WorkOrder
drop table if exists hibernate_sequences
