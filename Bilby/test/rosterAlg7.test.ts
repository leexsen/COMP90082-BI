import { rosterFile } from "../src/controllers/apiControllers";
import fs from "fs";

const buf = fs.readFileSync("test/data/BigIssueRostering.xlsx");

test("bookings on different days", () => {
  expect(() => rosterFile(new Date(2019, 7, 5), new Date(2019, 7, 9), buf)).not.toThrow();
});
