import { getBearerAccessTokenWithHeaderAttribute } from './storage-manager';

export type HeadersCallback = () => { [key: string]: string };

const createGetRequestOptions = (headers: HeadersCallback): RequestInit => {
  return {
    method: 'GET',
    headers: headers()
  };
};

const createPostRequestWithBearerHeader = (body: string): RequestInit => {
  return {
    method: 'POST',
    body: body,
    headers: getBearerAccessTokenWithHeaderAttribute()
  };
};

const createPutRequestWithBearerHeader = (body: string): RequestInit => {
  return {
    method: 'PUT',
    body: body,
    headers: getBearerAccessTokenWithHeaderAttribute()
  };
};

const createGetRequestWithBearerHeader = (): RequestInit => {
  return {
    method: 'GET',
    headers: getBearerAccessTokenWithHeaderAttribute()
  };
};

const createDeleteRequestWithBearerHeader = (): RequestInit => {
  return {
    method: 'DELETE',
    headers: getBearerAccessTokenWithHeaderAttribute()
  };
};

const createPostRequest = (body: string): RequestInit => {
  return {
    method: 'POST',
    body: body,
    headers: {
      'Content-Type': 'application/json'
    }
  };
};

const createPutRequest = (body: string): RequestInit => {
  return {
    method: 'PUT',
    body: body,
    headers: {
      'Content-Type': 'application/json'
    }
  };
};

const createGetRequest = (): RequestInit => {
  return {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json'
    }
  };
};

const createDeleteRequest = (): RequestInit => {
  return {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json'
    }
  };
};

export const applyGetRequestWithBearerHeader = async (url: string) => {
  try {
    const response = await fetch(url, createGetRequestWithBearerHeader());

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return response;
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};

export const applyPostRequestWithBearerHeader = async (url: string, body: string) => {
  try {
    return await fetch(url, createPostRequestWithBearerHeader(body));
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};

export const applyPutRequestWithBearerHeader = async (url: string, body: string) => {
  try {
    return await fetch(url, createPutRequestWithBearerHeader(body));
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};

export const applyDeleteRequestWithBearerHeader = async (url: string) => {
  try {
    const response = await fetch(url, createDeleteRequestWithBearerHeader());

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return response;
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};

export const applyGetRequest = async (url: string) => {
  try {
    const response = await fetch(url, createGetRequest());

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return response;
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};

export const applyPostRequest = async (url: string, body: string) => {
  try {
    return await fetch(url, createPostRequest(body));
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};

export const applyPutRequest = async (url: string, body: string) => {
  try {
    return await fetch(url, createPutRequest(body));
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};

export const applyDeleteRequest = async (url: string) => {
  try {
    const response = await fetch(url, createDeleteRequest());

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return response;
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};

export const applyGetRequestOptional = async (url: string, headers: HeadersCallback) => {
  try {
    const response = await fetch(url, createGetRequestOptions(headers));

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}, url: ${url}`);
    }

    return response;
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
};
