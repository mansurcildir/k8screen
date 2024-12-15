import { jwtDecode } from 'jwt-decode';

export interface CustomJwtPayload {
  username: string;
  exp: number;
}

export const isTokenExpired = (token: string) => {
  try {
    // Decode the token
    const decodedToken = jwtDecode(token);

    // Check if the token has an expiration date
    if (!decodedToken.exp) {
      return true;
    }

    return decodedToken.exp * 1000 < Date.now();
  } catch {
    return false;
  }
};
