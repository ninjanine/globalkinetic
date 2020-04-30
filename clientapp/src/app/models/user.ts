export class User {
    id: string;
    login: string;
    password: string;
    email: string;
    firstName: string;
    lastName: string;
    phone?: string;
    token: string;
    roles?: string[]
}