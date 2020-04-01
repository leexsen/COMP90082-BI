import { rosterFile } from "../src/controllers/apiControllers";
import fs from "fs";

const buf = fs.readFileSync("test/data/NoGood.xlsx");

test("invalid file", () => {
  expect(rosterFile(new Date(2019, 7, 5), new Date(2019, 7, 9), buf)).toEqual(null);
});
