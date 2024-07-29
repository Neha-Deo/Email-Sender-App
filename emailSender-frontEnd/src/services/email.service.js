
import { customAxios } from "./helper";

export async function sendEmail(emailData) {
    
    const result = (await customAxios.post('/email/sendEmail', emailData)).data;
    return result;

}