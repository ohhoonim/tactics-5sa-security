

SELECT user_id, external_id, status, created_at, created_by, modified_at, modified_by, username, employee_no, email, department_id, job_position, job_role, password, last_login_at, failed_login_attempt, auth_source FROM public.system_users u WHERE u.username = 'tester';
