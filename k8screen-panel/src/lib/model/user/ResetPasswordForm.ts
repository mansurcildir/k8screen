export interface ResetPasswordForm {
  password: string;
  confirmPassword: string;
  code: number | null;
}
