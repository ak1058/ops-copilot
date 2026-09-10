-- Database Seed Script for OpsCopilot

-- Clear existing data
DELETE FROM order_events;
DELETE FROM deliveries;
DELETE FROM payments;
DELETE FROM orders;
DELETE FROM customers;

-- Scenario 1: Order 4521, Payment SUCCESS, Delivery ASSIGNED
INSERT INTO customers (id, name, email, phone, created_at) VALUES (101, 'Alice Smith', 'alice@example.com', '555-0101', NOW());
INSERT INTO orders (id, customer_id, status, total_amount, currency, created_at, updated_at) VALUES (4521, 101, 'PROCESSING', 1499.00, 'INR', NOW(), NOW());
INSERT INTO payments (id, order_id, status, amount, payment_method, transaction_id, paid_at, created_at) VALUES (1, 4521, 'SUCCESS', 1499.00, 'UPI', 'TXN-4521-1', NOW(), NOW());
INSERT INTO deliveries (id, order_id, status, delivery_partner, tracking_number, expected_delivery_date, assigned_at) VALUES (1, 4521, 'ASSIGNED', 'BlueDart', 'TRK-4521', NOW() + INTERVAL '2 days', NOW());
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (1, 4521, 'ORDER_CREATED', 'Order placed', NOW());
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (2, 4521, 'PAYMENT_SUCCESS', 'Payment verified', NOW());

-- Scenario 2: Order 1289, Payment SUCCESS, Order CONFIRMED, Delivery NOT_ASSIGNED (Delivery Assignment Failed)
INSERT INTO customers (id, name, email, phone, created_at) VALUES (102, 'Bob Jones', 'bob@example.com', '555-0102', NOW());
INSERT INTO orders (id, customer_id, status, total_amount, currency, created_at, updated_at) VALUES (1289, 102, 'CONFIRMED', 2500.00, 'INR', NOW(), NOW());
INSERT INTO payments (id, order_id, status, amount, payment_method, transaction_id, paid_at, created_at) VALUES (2, 1289, 'SUCCESS', 2500.00, 'CREDIT_CARD', 'TXN-1289-1', NOW(), NOW());
INSERT INTO deliveries (id, order_id, status, delivery_partner, tracking_number, expected_delivery_date, assigned_at) VALUES (2, 1289, 'NOT_ASSIGNED', NULL, NULL, NULL, NULL);
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (3, 1289, 'ORDER_CREATED', 'Order placed', NOW());
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (4, 1289, 'PAYMENT_SUCCESS', 'Payment verified', NOW());
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (5, 1289, 'ORDER_CONFIRMED', 'Order confirmed by seller', NOW());
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (6, 1289, 'DELIVERY_ASSIGNMENT_FAILED', 'Failed to assign delivery partner due to capacity constraints', NOW());

-- Scenario 3: Order 2231, Payment PENDING, Delivery NOT_ASSIGNED
INSERT INTO customers (id, name, email, phone, created_at) VALUES (103, 'Charlie Brown', 'charlie@example.com', '555-0103', NOW());
INSERT INTO orders (id, customer_id, status, total_amount, currency, created_at, updated_at) VALUES (2231, 103, 'CREATED', 999.00, 'INR', NOW(), NOW());
INSERT INTO payments (id, order_id, status, amount, payment_method, transaction_id, paid_at, created_at) VALUES (3, 2231, 'PENDING', 999.00, 'UPI', NULL, NULL, NOW());
INSERT INTO deliveries (id, order_id, status, delivery_partner, tracking_number, expected_delivery_date, assigned_at) VALUES (3, 2231, 'NOT_ASSIGNED', NULL, NULL, NULL, NULL);
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (7, 2231, 'ORDER_CREATED', 'Order placed', NOW());

-- Scenario 4: Order 3344, Payment FAILED, Delivery NOT_ASSIGNED
INSERT INTO customers (id, name, email, phone, created_at) VALUES (104, 'Diana Prince', 'diana@example.com', '555-0104', NOW());
INSERT INTO orders (id, customer_id, status, total_amount, currency, created_at, updated_at) VALUES (3344, 104, 'CREATED', 1200.00, 'INR', NOW(), NOW());
INSERT INTO payments (id, order_id, status, amount, payment_method, transaction_id, paid_at, created_at) VALUES (4, 3344, 'FAILED', 1200.00, 'DEBIT_CARD', 'TXN-3344-1', NULL, NOW());
INSERT INTO deliveries (id, order_id, status, delivery_partner, tracking_number, expected_delivery_date, assigned_at) VALUES (4, 3344, 'NOT_ASSIGNED', NULL, NULL, NULL, NULL);
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (8, 3344, 'ORDER_CREATED', 'Order placed', NOW());
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (9, 3344, 'PAYMENT_FAILED', 'Insufficient funds', NOW());

-- Scenario 5: Order 5566, Payment SUCCESS, Delivery DELIVERED
INSERT INTO customers (id, name, email, phone, created_at) VALUES (105, 'Eve Adams', 'eve@example.com', '555-0105', NOW());
INSERT INTO orders (id, customer_id, status, total_amount, currency, created_at, updated_at) VALUES (5566, 105, 'DELIVERED', 8500.00, 'INR', NOW() - INTERVAL '3 days', NOW());
INSERT INTO payments (id, order_id, status, amount, payment_method, transaction_id, paid_at, created_at) VALUES (5, 5566, 'SUCCESS', 8500.00, 'NET_BANKING', 'TXN-5566-1', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days');
INSERT INTO deliveries (id, order_id, status, delivery_partner, tracking_number, expected_delivery_date, assigned_at, delivered_at) VALUES (5, 5566, 'DELIVERED', 'FedEx', 'TRK-5566', NOW(), NOW() - INTERVAL '2 days', NOW());
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (10, 5566, 'ORDER_CREATED', 'Order placed', NOW() - INTERVAL '3 days');
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (11, 5566, 'PAYMENT_SUCCESS', 'Payment verified', NOW() - INTERVAL '3 days');
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (12, 5566, 'ORDER_CONFIRMED', 'Order confirmed by seller', NOW() - INTERVAL '3 days');
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (13, 5566, 'DELIVERY_ASSIGNED', 'Assigned to FedEx', NOW() - INTERVAL '2 days');
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (14, 5566, 'OUT_FOR_DELIVERY', 'Package is out for delivery', NOW() - INTERVAL '1 day');
INSERT INTO order_events (id, order_id, event_type, description, created_at) VALUES (15, 5566, 'DELIVERED', 'Package delivered successfully', NOW());

-- Note: Order 999999 (Non-existent order) will simply not be inserted here.
