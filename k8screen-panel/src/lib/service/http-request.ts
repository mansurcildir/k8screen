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
}

export const applyPutRequest = async (url: string, body: string) => {
  try {
    return await fetch(url, createPutRequest(body));
  } catch (error) {
    console.error('Request failed', error);
    throw error;
  }
}

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
