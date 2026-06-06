SELECT s.*
FROM strategy s
WHERE NOT EXISTS (
    SELECT 1
    FROM meeting m
    JOIN meeting_template mt ON mt.id = m.meeting_template_id
    WHERE m.strategy_id = s.id
      AND mt.name = 'Test'
      AND m.created_at >= '2026-01-01'
)