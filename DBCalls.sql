CREATE DATABASE jdbcdb;
CREATE TABLE `student` (
`id` int Primary key, 
`studentName` varchar(25),
`age` int,
`timeInserted` DATE);
select * from student;

DELIMITER $$
CREATE PROCEDURE insertStudent(idNum int, Sname VARCHAR(25), age int)
BEGIN
INSERT INTO student(id, studentName, age, timeInserted) VALUES(idNum, Sname, age, now());
COMMIT;
END $$

DELIMITER $$
CREATE PROCEDURE deleteStudent(idNum int)
BEGIN
DELETE FROM STUDENT WHERE ID = idNum;
COMMIT;
END $$

DELIMITER $$
CREATE PROCEDURE updateName(idNum int, newName varchar(25))
BEGIN
UPDATE student SET studentName = newName WHERE id = idNum;
COMMIT;
END $$

DELIMITER $$
CREATE PROCEDURE updateAge(idNum int, newAge int)
BEGIN
UPDATE student SET age = newAge WHERE id = idNum;
COMMIT;
END $$