create table fee (id bigint generated by default as identity (start with 1), age_from integer not null, age_to integer not null, amount integer not null, currency varchar(255), date_from timestamp, date_to timestamp, gender integer, primary key (id))
create table ladder (id bigint generated by default as identity (start with 1), area varchar(255), challenge_span integer not null, country varchar(255), description varchar(255), gender integer, max_age integer not null, min_age integer not null, name varchar(255), primary key (id))
create table ladder_subscription (id bigint generated by default as identity (start with 1), message varchar(255), rank integer not null, status integer, subscription_date timestamp, ladder_id bigint, player_id bigint, primary key (id))
create table match (id bigint generated by default as identity (start with 1), date timestamp, ladder_id bigint not null, location varchar(255), loser_id bigint not null, score varchar(255), status integer, winner_id bigint not null, primary key (id))
create table payment (id bigint generated by default as identity (start with 1), amount bigint not null, currency varchar(255), date timestamp, transaction_id varchar(255), player_id bigint, primary key (id))
create table player (id bigint generated by default as identity (start with 1), area varchar(255), availability varchar(255), country varchar(255), dob timestamp, email varchar(255), firstname varchar(255), gender integer, last_connection_date timestamp, lastname varchar(255), membership_expiry_date timestamp, password varchar(255), phone varchar(255), role integer not null, sign_up_date timestamp, status integer, town varchar(255), username varchar(255), primary key (id))
create table ranking_history (id bigint generated by default as identity (start with 1), date timestamp, rank integer not null, ladder_id bigint, player_id bigint, primary key (id))
alter table ladder_subscription add constraint FK2lb6ofps0busfpfj9nbgahs3u foreign key (ladder_id) references ladder
alter table ladder_subscription add constraint FK82r4t9nng5n8e7u1tl212dvjp foreign key (player_id) references player
alter table payment add constraint FKdywn4k609vjn7hv1docm2hfcb foreign key (player_id) references player
alter table ranking_history add constraint FK80c7luuswbk97jp4ydc861idr foreign key (ladder_id) references ladder
alter table ranking_history add constraint FKt2xpgm8dp7xtv8migs7h5e3v9 foreign key (player_id) references player
