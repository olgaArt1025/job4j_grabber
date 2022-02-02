CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

SELECT c.id, c.name AS "Название компании", p.name AS "Ф.И.О"   
FROM person AS p join company AS c
ON p.company_id = c.id
WHERE c.id != 5;

SELECT c.name AS "Название компании",  COUNT(p.name)  AS "Количество человек"
FROM person AS p join company AS c
ON p.company_id = c.id
GROUP BY c.name
HAVING COUNT(p.name) =
    (SELECT MAX (a.cnt) FROM
		(SELECT COUNT(p.name) AS cnt FROM person AS p
		JOIN company as c ON p.company_id = c.id               
         GROUP BY (c.name)) AS a);


SELECT c.name as "Название компании",  count(p.name)  as "Количество человек"
from person as p join company as c
ON p.company_id = c.id
group by c.name
ORDER BY count(p.name) DESC
LIMIT  1;
		 
		 
		 