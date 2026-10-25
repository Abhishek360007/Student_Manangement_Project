INSERT INTO students (first_name, last_name, date_of_birth, email, index_number, department, is_on_budget) VALUES
	('Aarav', 'Sharma', '2002-04-14', 'aarav.sharma@example.com', 1001, 'Computer Science', true),
	('Priya', 'Verma', '2001-09-03', 'priya.verma@example.com', 1002, 'Electronics', false),
	('Rohan', 'Gupta', '2003-01-22', 'rohan.gupta@example.com', 1003, 'Mechanical', true),
	('Sneha', 'Patel', '2002-11-08', 'sneha.patel@example.com', 1004, 'Computer Science', false),
	('Vikram', 'Singh', '2000-06-30', 'vikram.singh@example.com', 1005, 'Civil', true);

INSERT INTO projects (project_name, project_description, student_id) VALUES
	('Library Management App', 'Spring Boot app to track book issues and returns', 1),
	('IoT Weather Station', 'Arduino based temperature and humidity logger', 2),
	('Robotic Arm', '3-axis robotic arm controlled over Bluetooth', 3),
	('Chat Application', 'Real-time chat using WebSockets', 4),
	('Bridge Load Analysis', 'Simulation of load distribution on a truss bridge', 5),
	('Portfolio Website', 'Personal portfolio built with React', 1);
