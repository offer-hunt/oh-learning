-- V2__add_score_feedback_to_question_states.sql
alter table learning.learning_question_states
    add column if not exists last_score numeric,
    add column if not exists last_feedback text;
