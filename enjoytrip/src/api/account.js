import { localAxios } from "@/util/http-commons";

const local = localAxios();

async function userJoin(param, success, fail) {
  console.log("join user param :", param);
  await local.post("/users", param).then(success).catch(fail);
  console.log("join user end");
}

async function dupCheck(param, success, fail) {
  console.log("dupCheck param :", param);
  await local.post("/users/dupCheck", param).then(success).catch(fail);
}

async function userConfirm(param, success, fail) {
  console.log("param", param);
  await local.post(`/users/login`, param).then(success).catch(fail);
  console.log("userConfirm ok");
}

async function findById(userid, success, fail) {
  local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
  await local.get(`/users/info/${userid}`).then(success).catch(fail);
}

async function tokenRegeneration(user, success, fail) {
  local.defaults.headers["refreshToken"] = sessionStorage.getItem("refreshToken"); //axios header에 refresh-token 셋팅
  await local.post(`/users/refresh`, user).then(success).catch(fail);
}

async function logout(userid, success, fail) {
  await local.get(`/users/logout/${userid}`).then(success).catch(fail);
}

// 아이디찾기, 비밀번호찾기
async function searchIdPwd(param, success, fail) {
  console.log("searchIdPwd param: ", param);
  local.post(`/users/search`, param).then(success).catch(fail);
}

// randomToken으로 아이디 식별
async function findByRandomToken(param, success, fail) {
  console.log("findByRandomToken param:", param);
  await local.get("/users/randomToken", { params: param }).then(success).catch(fail);
  console.log("findByRandomToken end!!");
}

async function modifyPwd(param, success, fail) {
  console.log("modify password param: ", param);
  await local.put("/users", param).then(success).catch(fail);
}

async function pwdcheck(param, success, fail) {
  console.log("pwd check param:", param);
  await local.post("/users/password", param).then(success).catch(fail);
}

export {
  userConfirm,
  findById,
  tokenRegeneration,
  logout,
  searchIdPwd,
  userJoin,
  dupCheck,
  findByRandomToken,
  modifyPwd,
  pwdcheck,
};