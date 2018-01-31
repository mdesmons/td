create table application_user (id bigint identity not null, last_login datetime2, location_codes varchar(255), password varchar(255), type int, username varchar(255), primary key (id))
create table customer (location_code varchar(255) not null, cachetdaccount varchar(255), controltdaccount varchar(255), haircut_account varchar(255), haircut_allowed bit not null, interesttdaccount varchar(255), monthly_interest_allowed bit not null, name varchar(255), natural_account varchar(255), natural_account_currency varchar(255), naturaltdaccount varchar(255), primary key (location_code))
create table term_deposit (id bigint identity not null, vbtclosing_date datetime2, account varchar(255), closing_date datetime2, currency varchar(255), daily_gross_client_interest double precision not null, daily_gross_customer_interest double precision not null, daily_haircut double precision not null, daily_net_client_interest double precision not null, dailywht double precision not null, haircut double precision not null, interest double precision not null, maturity_date datetime2, opening_date datetime2, payment_type int, principal double precision not null, reason_for_close int, source_account varchar(255), status int, term int not null, value_date datetime2, location_code varchar(255), primary key (id))
create table transfer (id bigint identity not null, amount double precision not null, currency varchar(255), date datetime2, narrative varchar(255), status int, type int, term_deposit_id bigint, primary key (id))
alter table term_deposit add constraint FKbdb5a066i5h8sbyijbfg7f537 foreign key (location_code) references customer
alter table transfer add constraint FKdqn7b3ogogvq06u5tw5k28k0g foreign key (term_deposit_id) references term_deposit
