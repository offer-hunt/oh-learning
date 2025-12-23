create table if not exists learning.learning_ai_task_stats
(
    user_id          uuid        not null,
    task_id          uuid        not null, -- он же question_id
    hints_used       int         not null default 0,
    last_request_at  timestamptz not null default now(),
    constraint learning_ai_task_stats_pk primary key (user_id, task_id)
);