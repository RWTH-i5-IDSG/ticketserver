INSERT INTO partner (partner_id, name) VALUES (1, 'Partner1');

INSERT INTO organisation (org_id, name) VALUES (162, 'KVB');
INSERT INTO organisation (org_id, name) VALUES (169, 'ASEAG');
INSERT INTO organisation (org_id, name) VALUES (x'882A'::int, 'Test-SAM PV 1');
INSERT INTO organisation (org_id, name) VALUES (x'888E'::int, 'Test-SAM PV 2');
INSERT INTO organisation (org_id, name) VALUES (x'888F'::int, 'Test-SAM PV 3');

INSERT INTO product (product_id, pv_org_id, means_of_transport_category_code) VALUES (1, 162, 1);
INSERT INTO product (product_id, pv_org_id, means_of_transport_category_code) VALUES (2, 162, 1);
INSERT INTO product (product_id, pv_org_id, means_of_transport_category_code) VALUES (1, x'882A'::int, 1);

-- two api tokens for the same (first) product, third one for the second product
INSERT INTO ticket_api_token (kvp_org_id, product_id, pv_org_id, partner_id, api_token) VALUES (169, 1, 162, 1, 'TICKET_API_TOKEN_1_STRING');
INSERT INTO ticket_api_token (kvp_org_id, product_id, pv_org_id, partner_id, api_token) VALUES (169, 1, 162, 1, 'TICKET_API_TOKEN_4_STRING');
INSERT INTO ticket_api_token (kvp_org_id, product_id, pv_org_id, partner_id, api_token) VALUES (169, 2, 162, 1, 'TICKET_API_TOKEN_2_STRING');
INSERT INTO ticket_api_token (kvp_org_id, product_id, pv_org_id, partner_id, api_token) VALUES (162, 1, x'882A'::int, 1, 'TICKET_API_TOKEN_3_STRING');

INSERT INTO log_api_token (api_token, pv_org_id) VALUES ('LOG_API_TOKEN_1_STRING', x'882A'::int);

INSERT INTO transaction_data (transaction_data_id, transaction_operator_id, terminal_number, terminal_org_id, location_number, location_org_id)
  VALUES (1, 162, 1, 162, 1, 162);

SELECT initialize_deployments_and_sequence_information(4);
SELECT use_transaction_data_for_all_products_and_deployments(1, 4);

INSERT INTO betreiber_key (chr, private_exponent, modulus) VALUES (
    E'\\x000000000000000000000000',
    E'\\xDAEDBEEF', -- private exponent in hex
    E'\\xDAEDBEEF' -- modulus
);
