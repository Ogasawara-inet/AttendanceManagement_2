create table if not exists employee (
	id serial not null,
	emp_id varchar(254),
	auth varchar(60),
	last_name varchar(60),
	middle_name varchar(60),
	first_name varchar(60),
	full_name varchar(60),
	last_name_kana varchar(60),
	middle_name_kana varchar(60),
	first_name_kana varchar(60),
	full_name_kana varchar(60),
	birthday date,
	password varchar(254),
	tel varchar(254),
	email varchar(254),
	location varchar(60),
	dept varchar(60),
	joining date,
	primary key (id),
	unique (emp_id)
);

create table if not exists monthly_report(
	id serial,
	submitted boolean,
	emp_id varchar(254),
	name varchar(254),
	index_month date,
	location varchar(60),
	dept varchar(60),
	approval_id varchar(254),
	approval_name varchar(254),
	approval_date date,
	primary key (id),
	unique (emp_id, index_month)
);

create table if not exists work_time(
	id serial,
	emp_id varchar(254),
	work_date date,
	division varchar(254),
	start_time time,
	finish_time time,
	break_start_time time,
	break_finish_time time,
	working_time integer,
	break_time integer,
	note varchar(50000),
	primary key (id),
	unique (emp_id, work_date)
);