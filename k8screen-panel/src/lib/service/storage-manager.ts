/**
 * Storage Manager
 * Available all client side
 */
const ACCESS_TOKEN_KEY = 'access-token';
const REFRESH_TOKEN_KEY = 'refresh-token';
const TOKEN_PREFIX = 'Bearer ';

/**
 * Function to set the tokens
 * @param accessToken represent the access token
 * @param refreshToken represent the refresh token
 */
export const setTokens = (accessToken: string, refreshToken: string) => {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
};

/**
 * Function to get the access token
 */
export const getAccessToken = () => {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
};

/**
 * Function to get the refresh token
 */
export const getRefreshToken = () => {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
};

/**
 * Function to get all tokens
 */
export const getAllTokens = () => {
  return {
    accessToken: getAccessToken(),
    refreshToken: getRefreshToken()
  };
};

/**
 *  Function to check if the tokens exist
 */
export const checkTokensExist = () => {
  return !!getAccessToken() && !!getRefreshToken();
};

/**
 * Function to get the bearer access token with header attribute
 */
export const getBearerAccessTokenWithHeaderAttribute = () => {
  return {
    Authorization: `${TOKEN_PREFIX}${getAccessToken()}`,
    'Content-Type': 'application/json'
  };
};

/**
 * Function to get the bearer access token
 */
export const getBearerAccessToken = () => {
  const accessToken = getAccessToken();
  if (!accessToken) {
    return null;
  }
  return `${TOKEN_PREFIX}${accessToken}`;
};

/**
 * Function to clear the tokens
 */
export const clearTokens = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
};
