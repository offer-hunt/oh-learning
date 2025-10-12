create schema if not exists learning;

do $$
    begin
        if not exists (select from pg_roles where rolname = 'learning_user') then
            create user learning_user with password 'learning_user';
        end if;
    end$$;

grant usage on schema learning to learning_user;
grant create on schema learning to learning_user;

grant all on all tables in schema learning to learning_user;
grant all on all sequences in schema learning to learning_user;

alter default privileges in schema learning grant all on tables to learning_user;
alter default privileges in schema learning grant all on sequences to learning_user;

alter role learning_user in database oh_learning set search_path = learning;

revoke all on schema public from learning_user;
