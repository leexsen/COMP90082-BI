import express from "express";
import * as controller from "../controllers/apiControllers";

const router = express.Router();

router.post("/upload", controller.upload);

export default router;
