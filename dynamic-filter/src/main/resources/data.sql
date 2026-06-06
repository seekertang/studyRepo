-- ============================================================
-- meeting_template
-- ============================================================
INSERT INTO meeting_template (id, name) VALUES
(1, 'Internal Review'),
(2, 'External Quarterly'),
(3, 'Internal Planning'),
(4, 'Board Update'),
(5, 'Internal Budget');

-- ============================================================
-- meeting_partner
-- ============================================================
INSERT INTO meeting_partner (id, name) VALUES
(1, 'External Partner One'),
(2, 'Internal Partner Ops');

-- ============================================================
-- strategy
-- Notes (matching the filter conditions in filter-example-or-not-conditions.json):
--   Condition 1: status IN ('ACTIVE','INACTIVE')
--   Condition 2: aum BETWEEN 1000000 AND 9999999
--   Condition 3: NOT EXISTS any Meeting that matches the disqualifying conditions
--   Condition 4: ownerName IS NOT NULL
-- ============================================================
INSERT INTO strategy (id, name, status, owner_name, aum, updated_at) VALUES
-- Match: ACTIVE + valid aum + non-null ownerName + no disqualifying Meeting
(1, 'Alpha Growth Fund Ethan',    'ACTIVE',   'Alice Wang',   3500000.00, '2026-03-15T10:00:00'),
-- Match: INACTIVE + valid aum + non-null ownerName + no disqualifying Meeting
(2, 'Beta Value Strategy',  'INACTIVE', 'Bob Chen',     1200000.00, '2026-02-20T09:30:00'),
-- Excluded: status = 'CLOSED' (not in the OR group)
(3, 'Gamma Closed Fund',    'CLOSED',   'Carol Li',     5000000.00, '2026-01-10T08:00:00'),
-- Excluded: aum is outside the BETWEEN range
(4, 'Delta Mega Fund',      'ACTIVE',   'David Zhao',  50000000.00, '2026-03-01T11:00:00'),
-- Excluded: ownerName is NULL (fails the NOT IS_NULL condition)
(5, 'Epsilon No Owner',     'ACTIVE',   NULL,           2000000.00, '2026-03-10T14:00:00'),
-- Excluded: has a disqualifying Meeting (meetingTemplate.name STARTS_WITH 'Internal')
(6, 'Zeta Internal Flag',   'ACTIVE',   'Eva Sun',      4500000.00, '2026-04-01T16:00:00'),
-- Excluded: has a disqualifying Meeting (createdAt < 2025-01-01)
(7, 'Eta Old Meeting Fund', 'ACTIVE',   'Frank Wu',     3000000.00, '2026-03-25T09:00:00'),
-- Match: ACTIVE + valid aum + non-null ownerName + Meeting uses a non-Internal template and valid date
(8, 'Theta Clean Fund',     'ACTIVE',   'Grace Liu',    8800000.00, '2026-04-10T08:30:00');

-- ============================================================
-- meeting
-- ============================================================
INSERT INTO meeting (id, strategy_id, meeting_template_id, partner_id, created_at) VALUES
-- strategy 1: uses an External template and valid date -> does not trigger NOT EXISTS exclusion
(1,  1, 2, 1, '2026-01-15T10:00:00'),
-- strategy 2: has no Meeting -> does not trigger NOT EXISTS exclusion
-- strategy 6: uses an Internal template -> triggers NOT EXISTS exclusion
(2,  6, 1, 2, '2026-02-01T10:00:00'),
-- strategy 7: createdAt is earlier than 2025-01-01 -> triggers NOT EXISTS exclusion
(3,  7, 4, 2, '2024-12-01T10:00:00'),
-- strategy 8: uses an External template and valid date -> does not trigger exclusion
(4,  8, 2, 1, '2026-03-01T10:00:00'),
(5,  8, 4, 2, '2026-03-20T14:00:00');

