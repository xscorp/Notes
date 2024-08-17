## SQL Injection


### SQL Injection UNION Attack

We do UNION attack to inject arbitrary SQL command alongside a SELECT query. For an example, If the following query is being executed:

```
SELECT account_id, account_secret FROM Accounts WHERE account_username='demouser'
```

We can inject our own SQL query through UNION operator, which is used for multiple queries:

```
SELECT account_id, account_secret FROM Accounts where account_username='demouser' UNION SELECT username, password FROM users -- 
```

But this approach will not work because as per SQL Syntax When two queries are chained with a UNION, Both needs to be printing same number and same type of columns. So In this case, The first query has `2` columns of first `int` and second `string` type, So the second query must also have `2` columns of first `int` and second `string` type. Since both `username` and `password` are usually `string`, The query will fail.

<br/>

#### The Purpose of NULL

Here we can utilize only one column which is common - the second column. The second column in both the queries have `string` data type. But what should we print in place of username? We can use 'NULL' for that. 'NULL' type in SQL is compatible will all the types so we can use it anywhere.
So the query will be:

```
SELECT account_id, account_secret FROM Accounts where account_username='demouser' UNION SELECT NULL, password FROM users -- 
```

Running this query will result in database returning the original set of data it was returning for first SQL query followed by pairs of NULL and passwords.

<br/>

#### The purpose of Column Concatanation

But hey, We need usernames too. How can we print data of two columns in a single column? For that we can utilize string concatanation. For example, The `||` symbol in MySQL DB is used for string concatanation. So we can utilize it to combine data from two columns like this:

```
SELECT column1 || '#' || column2 from table1
```

It will result in data of both column1 and column2 being displayed in a single column separated by `#`. For example `column1data#column2data`.

So we can utilize this technique to print both the username and password information in a single column on the attack demonstrated previously:

```
SELECT account_id, account_secret FROM Accounts where account_username='demouser' UNION SELECT NULL, username || '~' || password FROM users -- 
```

This will result in database returning the result of first query followed by NULL and pairs of username and password separated by `~`. For example:
```
NULL | admin~password@123
NULL | demouser~demopass
```

<br/>

#### Identifying Number of Columns through UNION and ORDER BY Attack

Using UNION to extract data from other tables and columns is all cool, But how do you identify the number of tables/columns in the first place. Lets consider we have to identify the number of columns a table has.

Since NULL is compatible with all the types, We can place it to construct a SELECT query with right number of columns.

```
SELECT null, null FROM Accounts   => OK
SELECT null, null, null FROM Accounts   => OK
SELECT null, null, null, null FROM Accounts   => OK
SELECT null, null, null, null, null FROM Accounts   => ERROR
```

This will signify that Accounts table only has 4 columns as we started getting errors beyond it. Another way to achieve the same is through ORDER BY. Each column can be referred by its column number. Like we can refer to the first column as `1`, second as `2` and so on. ORDER BY is used for requesting the data from database in a certain order of a certain column. We can refer that column by the column number. If we specify a column number that doesn't exist, The database throws errors. We utilize this fact to identify the correct number of columns:

```
' ORDER BY 1     => OK
' ORDER BY 2     => OK
' ORDER BY 3     => OK
' ORDER BY 4     => OK
' ORDER BY 5     => ERROR
```

This again signifies that there are only 4 columns in the table.
