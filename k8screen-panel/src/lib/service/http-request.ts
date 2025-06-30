import { authAPI } from './auth-service';
import { getBearerAccessTokenWithHeaderAttribute, getRefreshTokenWithHeaderAttribute } from './storage-manager';

const createGetRequestWithRefreshHeader = (): RequestInit => {
  return {
    method: 'GET',
    headers: getRefreshTokenWithHeaderAttribute()
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
  await authAPI.authorize();
  const response = await fetch(url, createGetRequestWithBearerHeader());

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};

export const applyPostRequestWithBearerHeader = async (url: string, body: string) => {
  await authAPI.authorize();
  const response = await fetch(url, createPostRequestWithBearerHeader(body));

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};

export const applyPutRequestWithBearerHeader = async (url: string, body: string) => {
  await authAPI.authorize();
  const response = await fetch(url, createPutRequestWithBearerHeader(body));
  console.log(response);

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};

export const applyDeleteRequestWithBearerHeader = async (url: string) => {
  await authAPI.authorize();
  const response = await fetch(url, createDeleteRequestWithBearerHeader());

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};

export const applyGetRequest = async (url: string) => {
  const response = await fetch(url, createGetRequest());

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};

export const applyPostRequest = async (url: string, body: string) => {
  const response = await fetch(url, createPostRequest(body));

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};

export const applyPutRequest = async (url: string, body: string) => {
  const response = await fetch(url, createPutRequest(body));

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};

export const applyDeleteRequest = async (url: string) => {
  const response = await fetch(url, createDeleteRequest());

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};

export const applyGetRequestWithRefreshToken = async (url: string) => {
  const response = await fetch(url, createGetRequestWithRefreshHeader());

  if (!response.ok) {
    throw await response.json();
  }

  return response;
};
