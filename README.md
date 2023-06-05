
# Metropolis CSV Exercise
## Instructions
This excercise is designed to be completed in 1 to 2 hours. During the excercise, feel free to use any coding language, library, and resources of your choosing. Note that the code written for this excercise will be used in a followup interview so be prepared to explain the logic and make modifications.

After completing the exercise or exhausting the 2 hours, please send back your code along with the printed solutions to each step.

Please provide your printed solutions in a `answers.txt` document along with your answers to [Questions](#questions).

### Example
```
Questions
For Excercise B, what time zone should be considered when filtering for weekends?
- This is my answer to this question.

Exercise A.
{
    "472": 549.0,
    "473": 7891.0,
    "474": 1257.0
}

Exercise B.
{
    "472": 35.0,
    "473": 11.0,
    "474": 141.0
}

Exercise C.
{
    "472": {
        "39": 468.0,
        "40": 214.0,
    },
    "473": {
        "39": 139.0,
        "40": 181.0,
    }
}

Exercise D.
{
    "472": {
        "38->39": 468.0,
        "39->40": -254.0
    },
    "473": {
        "38->39": 139.0,
        "39->40": 42.0
    }
}
```


### Notes for the solution
1. Ensure that the data being processed in the code is properly validated based on the [Table Schema](#table-schema) provided below.
   1. Appropriately convert data into the correct type
   2. Appropriately handle data that is `NULL`
   3. `payment_status` is guaranteed to be one of the statuses defined in the schema.
2. Do your best to write clean and optimized code. (It is not required to overoptimize. Treat it as if this were code for a real job).

## Definitions
`transaction` - A record of a vehicle's visit to a parking lot

`site` - A physical parking lot location where vehicle transactions are monitored

## Exercise
1. Setup a CSV reader to ingest the CSV file
1. For each of the exercises below, please print the answer in a list ordered by `site_id`
   2. Table explanation can be found at [Table Schema](#table-schema)
1. Exercise A: For the entirety of the data, find the total revenue by `site`, taking `payment_status` into consideration.
   1. Payment Foregone - We were unable to collect revenue because of a reason such as the card was declined
   1. Payment Not Required - Payment for the `transaction` was not required due to something like the total `transaction` time being under a grace period (10 minutes).
   1. Payment Completed - We collected revenue for this `transaction`
1. Exercise B: Filter to only “weekend” `transactions` (`transactions` that start on Saturday or Sunday)
1. Exercise C: For each week (ISO week), find the total weekly revenue by `site`
   1. A week is the week of the year, which is 0-53. Do not worry about year for this exercise.
   2. Example:
   ```
   Site 43, Week 3, $50
   Site 43, Week 4, $75
   Site 43, Week 5, $0
   Site 91, Week 3, $20
   Site 91, Week 4, $67
   Site 91, Week 5, $66
   ```
1. Exercise D: Determine Week-over-Week revenue change ($) for each site
   1. A week with no data should be considered to have $0 of revenue for that week.
   2. Example based on Excercise C example
   ```
   Site 43, Week 2-3, +$50
   Site 43, Week 3-4, +$25
   Site 43, Week 4-5, -$75
   Site 91, Week 2-3, +$20
   Site 91, Week 3-4, +$47
   Site 91, Week 4-5, -$1
   ```

### Questions
In addition to solving the exercises, please answer the following question.
1. For Excercise B, what time zone should be considered when filtering for weekends?
    1. Consider that I own parking lots (`sites`) in California, Texas, and New York

## Setup
Attached is a csv file name "metropolis_visits_iso8601.csv". It is a table of parking `transaction` data with the following columns.

### Table Schema
| Column | Type | Description |
| ------ | ---- | ----------- |
| transaction_id | LONG, AUTO INCREMENT, PRIMARY KEY, NOT NULL | The running index of transactions |
| site_id | LONG, FOREIGN KEY, NOT NULL | The ID of the parking lot |
| user_id | LONG, FOREIGN KEY, NOT NULL | The ID of the user_id |
| vehicle_id | LONG, FOREIGN KEY, NOT NULL | The ID of the vehicle that was detected |
| payment_status | VARCHAR, NOT NULL | This is a tristate column. It can be “Payment Foregone”, “Payment Not Required”, or “Payment Completed” |
| entry_time | DATETIME, NOT NULL | The entry time in ISO8601 datetime with offset |
| exit_time | DATETIME, NULL | The exit time in ISO8601 datetime with offset. Note that a vehicle might not have yet exited |
| price | FLOAT, NULL | The calculated price of the transaction. Note that this can only be computed when the transaction ends. |

Copy this file (located in the same zip) into your local environment and begin the exercises.
